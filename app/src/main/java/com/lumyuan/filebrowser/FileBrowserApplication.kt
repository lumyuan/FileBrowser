package com.lumyuan.filebrowser

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.lumyuan.androidfilemanager.AndroidFileManagerApplication
import com.lumyuan.filebrowser.pojo.GameBean
import com.lumyuan.filebrowser.utils.AppInfoProvider

class FileBrowserApplication : Application() {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate() {
        super.onCreate()
        AndroidFileManagerApplication.init(this)
        Thread{
            appList = AppInfoProvider().allAppsMap
        }.start()
    }

    companion object {
        var appList = HashMap<String, GameBean>() as Map<String, GameBean>
    }
}