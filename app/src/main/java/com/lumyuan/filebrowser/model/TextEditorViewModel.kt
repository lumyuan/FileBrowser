package com.lumyuan.filebrowser.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TextEditorViewModel: ViewModel() {
    val textContent = MutableLiveData<ByteArray>()
    val charset = MutableLiveData(Charsets.UTF_8)
    val size = MutableLiveData(0)
}