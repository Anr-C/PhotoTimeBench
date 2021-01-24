package com.lckiss.photobench.vm

import android.content.Context
import android.media.ExifInterface
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lckiss.photobench.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

class FileViewModel : ViewModel() {

    companion object {
        private const val TAG = "FileViewModel"
        const val SD_ROOT_PATH = "/storage/emulated/0/"
    }

    private val backStack = Stack<File>()

    var fileListData = MutableLiveData<Array<File>>()

    fun init() {
        File(SD_ROOT_PATH).jump()
    }

    fun listFiles(file: File) {
        backStack.add(file.parentFile)
        file.jump()
    }

    fun tryBack(): Boolean {
        return if (backStack.isNotEmpty()) {
            backStack.pop()?.also {
                it.jump()
            }
            true
        } else {
            false
        }
    }

    private val curDir
        get() = fileListData.value?.firstOrNull()?.parentFile

    fun refreshList() {
        curDir?.jump()
    }

    fun doTrans(context: Context) {
        fileListData.value?.forEach {
            if (it.name.matches(IMGRegex)) {
                val split = it.name.split("_")
                val yearStr = split[1]
                val hourStr = split[2]

                it.lastModify(yearStr, hourStr)

                if (it.isSupportExif()) {
                    val exifInterface = ExifInterface(it.absolutePath)
                    val exifTime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME)
                    if (exifTime.isNullOrEmpty()) {
                        val (year, month, day) = yearStr.YearMD() ?: return@forEach
                        val (hour, minute, sec) = hourStr.HourMS() ?: return@forEach
                        exifInterface.setAttribute(ExifInterface.TAG_DATETIME, "$year:$month:$day $hour:$minute:$sec")
                        Log.d(TAG, "file to exif time: $year:$month:$day $hour:$minute:$sec")
                    } else {
                        Log.d(TAG, "read exif time: $exifTime")
                    }
                }
            } else {
                if (it.isSupportExif()) {
                    val exifInterface = ExifInterface(it.absolutePath)
                    val exifTime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME)
                    if (!exifTime.isNullOrEmpty()) {
                        val parse = exifFormat.parse(exifTime) ?: return@forEach
                        val time = parseFormat.format(parse).split("-")
                        it.lastModify(time[0], time[1])
                        Log.d(TAG, "exif to file time: $exifTime")
                    }
                }
            }
        }
        context.refreshMedia(curDir?.absolutePath)
        refreshList()
    }

    private fun File.jump() {
        if (this.isDirectory)
            viewModelScope.launch(Dispatchers.IO) {
                val listFiles = this@jump.listFiles()
                postValue(listFiles)
            }
    }

    private suspend fun postValue(listFiles: Array<File>?) {
        withContext(Dispatchers.Main) {
            fileListData.value = listFiles
        }
    }
}