package com.lumyuan.filebrowser.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lumyuan.filebrowser.pojo.SimpleFileBean

class FileChangedViewModel: ViewModel() {
    val filePath: MutableLiveData<ArrayList<SimpleFileBean>> = MutableLiveData()
    fun getPath(): String {
        val stringBuilder = StringBuilder()
        for (index in filePath.value!!){
            if (index.name != "")
                stringBuilder.append("/${index.name}")
        }
        return if (stringBuilder.toString() == "") "/" else stringBuilder.toString()
    }
}