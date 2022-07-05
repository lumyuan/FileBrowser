package com.lumyuan.filebrowser.config

import android.content.Context
import com.google.gson.Gson
import com.lumyuan.androidfilemanager.FileUtils
import com.lumyuan.filebrowser.pojo.EditorConfigBean
import com.lumyuan.filebrowser.utils.DensityUtil
import java.io.File

class EditorConfigManager(private val context: Context) {

    private val path: String = context.filesDir.absolutePath + "/cache/editor configs/editor_config.json"
    private val file: File = File(path)
    private val gson = Gson()
    private var config: EditorConfigBean
    init {
        if (!file.parentFile!!.exists()){
            File(file.parent!!).mkdirs()
            file.createNewFile()
            config = EditorConfigBean(DensityUtil.sp2px(context, 16F).toFloat())
            saveConfig(config)
        }else {
            config = loadConfig()
        }
    }

    fun loadConfig(): EditorConfigBean {
        val json = FileUtils.readText(path)
        config = gson.fromJson(json, EditorConfigBean::class.java)
        return config
    }

    fun saveConfig(editorConfigBean: EditorConfigBean){
        config = editorConfigBean
        val json = gson.toJson(config)
        FileUtils.writeText(path, json)
    }

}