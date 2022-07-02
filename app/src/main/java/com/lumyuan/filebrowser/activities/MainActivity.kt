package com.lumyuan.filebrowser.activities

import android.os.Bundle
import android.os.Environment
import android.view.KeyEvent
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.lumyuan.androidfilemanager.file.File
import com.lumyuan.androidfilemanager.utils.ExternalStoragePermissionsUtils
import com.lumyuan.filebrowser.base.BaseActivity
import com.lumyuan.filebrowser.databinding.ActivityMainBinding
import com.lumyuan.filebrowser.model.FileChangedViewModel
import com.lumyuan.filebrowser.pojo.SimpleFileBean
import com.lumyuan.filebrowser.ui.widget.adapter.LabelListAdapter
import com.lumyuan.filebrowser.ui.widget.adapter.SimpleFileListAdapter
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : BaseActivity() {

    private val binding by binding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.root

        ExternalStoragePermissionsUtils.getExternalStoragePermission(this)
        ExternalStoragePermissionsUtils.getAllFilesAccessPermission(this)
        ExternalStoragePermissionsUtils.getApplicationExclusiveDirectoryPermission(this)
        initLabelView()
        initSimpleFileView()
        initFilePath()
    }

    private lateinit var labelListAdapter: LabelListAdapter
    private fun initLabelView() {
        labelListAdapter = LabelListAdapter(this@MainActivity, binding.labelList)
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        linearLayoutManager.stackFromEnd = true
        binding.labelList.layoutManager = linearLayoutManager
        binding.labelList.adapter = labelListAdapter
    }

    private lateinit var simpleFileListAdapter: SimpleFileListAdapter
    private fun initSimpleFileView() {
        simpleFileListAdapter = SimpleFileListAdapter(this, binding.simpleList)
        val layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        binding.simpleList.layoutManager = layoutManager
        binding.simpleList.adapter = simpleFileListAdapter
    }

    private lateinit var viewModel: FileChangedViewModel
    private fun initFilePath() {
        viewModel = ViewModelProvider(this).get(FileChangedViewModel::class.java)
        viewModel.filePath.observe(this){
            if (it != null){
                labelListAdapter.updateList(it)
                Observable.create { fileList ->
                    val list = ArrayList<String>()
                    fileList.onNext(viewModel.getPath())
                    fileList.onComplete()
                }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { fileList ->
                        simpleFileListAdapter.updateList(fileList)
                    }
            }
        }
        if (viewModel.filePath.value == null) {
            Thread{
                val fileList = ArrayList<SimpleFileBean>()
                val path = Environment.getExternalStorageDirectory().absolutePath
                for (i in path.split("/")){
                    fileList.add(SimpleFileBean().apply {
                        this.name = i
                    })
                }
                runOnUiThread {
                    viewModel.filePath.value = fileList
                }
            }.start()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode){
            KeyEvent.KEYCODE_BACK -> {
                if (viewModel.filePath.value!!.size > 1){
                    val newFiles = ArrayList<SimpleFileBean>()
                    for (i in 0 until viewModel.filePath.value!!.size - 1){
                        newFiles.add(viewModel.filePath.value!![i])
                    }
                    viewModel.filePath.value = newFiles
                    return true
                }else {
                    finish()
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

}