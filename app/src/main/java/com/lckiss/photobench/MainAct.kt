package com.lckiss.photobench

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.lckiss.photobench.adapter.FileListAdapter
import com.lckiss.photobench.databinding.ActMainBinding
import com.lckiss.photobench.dialog.HelpDialog
import com.lckiss.photobench.dialog.TimePickDialog
import com.lckiss.photobench.vm.FileViewModel
import com.yanzhenjie.permission.AndPermission

class MainAct : AppCompatActivity(), View.OnClickListener {

    private lateinit var mainBinding: ActMainBinding
    private lateinit var fileViewModel: FileViewModel
    private var fileListAdapter: FileListAdapter = FileListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        fileViewModel = ViewModelProvider(this).get(FileViewModel::class.java)

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mainBinding.rv.layoutManager = linearLayoutManager
        mainBinding.rv.adapter = fileListAdapter

        fileListAdapter.onItemClickListener = {
            if (it.isDirectory) {
                fileViewModel.listFiles(it)
            } else {
                timePickDialog.show(supportFragmentManager, "pick")
                timePickDialog.onDatePickListener = { date ->
                    val lastModified = it.setLastModified(date)
                    Log.d(TAG, "onDatePickListener: $date $date res: $lastModified")
                }
            }
        }

        fileViewModel.fileListData.observe(this) {
            fileListAdapter.data = it
        }

        initListener()
        initPermission()
    }

    private fun initListener() {
        arrayOf(
            mainBinding.back,
            mainBinding.refresh,
            mainBinding.help,
            mainBinding.trans
        ).forEach {
            it.setOnClickListener(this)
        }
    }

    private fun initPermission() {
        AndPermission.with(this)
            .runtime()
            .permission(
                arrayOf(
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.WRITE_EXTERNAL_STORAGE"
                )
            )
            .onGranted { permissions: List<String?>? ->
                showData()
            }
            .onDenied { permissions: List<String?>? ->
                toast("必须给予读写权限才能正常使用")
            }
            .start()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (fileViewModel.tryBack())
                return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun showData() {
        fileViewModel.init()
    }

    override fun onClick(v: View?) {
        v ?: return
        when (v.id) {
            R.id.back -> {
                fileViewModel.tryBack()
            }
            R.id.refresh -> {
                fileViewModel.refreshList()
            }
            R.id.help -> {
                showHelp()
            }
            R.id.trans -> {
                fileViewModel.doTrans(this)
            }
        }
    }


    private fun showHelp() {
        helpDialog.show(supportFragmentManager, "help")
    }

    private val helpDialog by lazy {
        HelpDialog()
    }

    private val timePickDialog by lazy {
        TimePickDialog()
    }

    companion object {
        private const val TAG = "MainAct"
    }

}

