package com.lumyuan.filebrowser.activities

import android.os.Bundle
import android.os.Environment
import android.view.KeyEvent
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
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

class MainActivity : BaseActivity() {

    private val binding by binding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ExternalStoragePermissionsUtils.getExternalStoragePermission(this)
        ExternalStoragePermissionsUtils.getAllFilesAccessPermission(this)
        ExternalStoragePermissionsUtils.getApplicationExclusiveDirectoryPermission(this)
        initLabelView()
        initSimpleFileView()
        initFilePath()
    }

    private lateinit var viewModel: FileChangedViewModel
    private lateinit var labelListAdapter: LabelListAdapter
    private fun initLabelView() {
        viewModel = ViewModelProvider(this).get(FileChangedViewModel::class.java)
        labelListAdapter = LabelListAdapter(this, binding.labelList)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        layoutManager.stackFromEnd = true
        binding.labelList.layoutManager = layoutManager
        binding.labelList.adapter = labelListAdapter
    }

    private lateinit var simpleFileListAdapter: SimpleFileListAdapter
    private fun initSimpleFileView() {
        simpleFileListAdapter = SimpleFileListAdapter(this, binding.simpleList)
        val layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        binding.simpleList.layoutManager = layoutManager
        binding.simpleList.adapter = simpleFileListAdapter

        binding.simpleList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    val lm = recyclerView.layoutManager as StaggeredGridLayoutManager
                    val mFirstVisibleItems = lm.findFirstVisibleItemPositions(IntArray(layoutManager.spanCount))
                    viewModel.listPosition.value = mFirstVisibleItems[0]
                }
            }
        })
    }

    private fun initFilePath() {
        java.io.File("").listFiles()
        //更新路径以及UI视图
        viewModel.filePath.observe(this){
            if (it != null){
                if ( it.size - labelListAdapter.list.size == 1){
                    labelListAdapter.addList(it[it.size - 1])
                }else if (labelListAdapter.list.size - it.size == 1){
                    labelListAdapter.deleteList(it.size)
                }else if (labelListAdapter.list.size - it.size > 1){
                    labelListAdapter.jumpList(it.size)
                }else {
                    labelListAdapter.updateList(it)
                }
                Observable.create { fileList ->
                    fileList.onNext(viewModel.getPath())
                    fileList.onComplete()
                }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { fileList ->
                        simpleFileListAdapter.updateList(fileList)
                    }
            }
        }

        //初始化路径
        val path = if (viewModel.filePath.value == null)
            Environment.getExternalStorageDirectory().absolutePath
        else
            viewModel.getPath()
        if (viewModel.filePath.value == null){
            Thread{
                val fileList = ArrayList<SimpleFileBean>()
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