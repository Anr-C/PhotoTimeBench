package com.lckiss.photobench

import android.content.Context
import android.media.MediaScannerConnection
import android.util.Log
import android.widget.Toast
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "Util"
private const val IMG_REGEX = "IMG_[0-9]{8}_[0-9]{6}\\S*"
private const val YEAR_REGEX = "[0-9]{8}"
private const val HOUR_REGEX = "[0-9]{6}\\S*"

val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
fun Long.transTime(): String {
    return simpleDateFormat.format(this)
}

val exifFormat = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault())
val parseFormat = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault())

val IMGRegex = Regex(IMG_REGEX)

val yearRegex = Regex(YEAR_REGEX)

val hourRegex = Regex(HOUR_REGEX)

fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.refreshMedia(path: String?) {
    path ?: return
    val file = File(path)
    if (!file.exists()) {
        Log.e(TAG, "freshMedia: file not found !")
        return
    }
    val paths = if (file.isFile) {
        arrayOf(path)
    } else {
        val des = mutableListOf<String>()
        file.listFiles()?.mapTo(des, { it.absolutePath })?.toTypedArray() ?: emptyArray()
    }
    Log.d(TAG, "freshMedia: $paths")
    MediaScannerConnection.scanFile(this, paths, null) { _, _ -> }
}

fun File.lastModify(ymd: String?, hms: String?) {
    if (ymd.isNullOrEmpty()) return
    val (year, month, day) = ymd.YearMD() ?: return
    val calendar = Calendar.getInstance()
    calendar.set(year.toInt(), month.toInt() - 1, day.toInt())
    this.setLastModified(calendar.time.time)

    if (hms.isNullOrEmpty()) return
    val (hour, minute, sec) = hms.HourMS() ?: return
    calendar.set(Calendar.HOUR_OF_DAY, hour.toInt())
    calendar.set(
        year.toInt(),
        month.toInt() - 1,
        day.toInt(),
        hour.toInt(),
        minute.toInt(),
        sec.toInt()
    )
    this.setLastModified(calendar.time.time)
}

fun File.isSupportExif(): Boolean {
    return this.name.endsWith(".jpg") || this.name.endsWith(".jpeg") || this.name.endsWith(".JPG") || this.name.endsWith(".JPEG")
}

fun String.YearMD(): Triple<String, String, String>? {
    return if (this.matches(yearRegex)) {
        Triple(this.substring(0..3), this.substring(4..5), this.substring(6..7))
    } else {
        null
    }
}

fun String.HourMS(): Triple<String, String, String>? {
    return if (this.matches(hourRegex)) {
        Triple(this.substring(0..1), this.substring(2..3), this.substring(4..5))
    } else {
        null
    }
}