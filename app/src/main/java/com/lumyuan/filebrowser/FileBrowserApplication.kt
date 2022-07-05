package com.lumyuan.filebrowser

import android.app.Application
import android.content.pm.ApplicationInfo
import com.lumyuan.androidfilemanager.AndroidFileManagerApplication
import com.lumyuan.filebrowser.crash.CrashHandler
import com.lumyuan.filebrowser.pojo.GameBean
import com.lumyuan.filebrowser.utils.AppInfoProvider

class FileBrowserApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val info: ApplicationInfo = applicationInfo
        if ((info.flags and ApplicationInfo.FLAG_DEBUGGABLE) == 0) {
            CrashHandler.init(this)
        }
        AndroidFileManagerApplication.init(this)
        Thread{
            appList = AppInfoProvider().allAppsMap
        }.start()
    }

    companion object {
        var appList = HashMap<String, GameBean>() as Map<String, GameBean>
    }
}