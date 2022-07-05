package com.lumyuan.filebrowser.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lumyuan.androidfilemanager.dao.FIleOrderRules
import com.lumyuan.filebrowser.pojo.SimpleFileBean
import com.lumyuan.filebrowser.ui.widget.adapter.SimpleFileListAdapter

class FileChangedViewModel: ViewModel() {
    val filePath: MutableLiveData<ArrayList<SimpleFileBean>> = MutableLiveData()
    val listPosition: MutableLiveData<Int> = MutableLiveData(0)
    val orderRules: MutableLiveData<Int> = MutableLiveData(FIleOrderRules.ORDER_BY_NAME + FIleOrderRules.ORDER_BY_SIMPLE)
    val action = MutableLiveData(SimpleFileListAdapter.ACTION_LOAD)
    fun getPath(): String {
        val stringBuilder = StringBuilder()
        for (index in filePath.value!!){
            if (index.name != "")
                stringBuilder.append("/${index.name}")
        }
        return if (stringBuilder.toString() == "") "/" else stringBuilder.toString()
    }
}