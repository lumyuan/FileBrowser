package com.lumyuan.androidfilemanager

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.lumyuan.androidfilemanager.dao.FileSettingsBean
import com.lumyuan.androidfilemanager.utils.Busybox
import com.lumyuan.androidfilemanager.utils.FileWrite
import com.lumyuan.androidfilemanager.utils.ShellExecutor

@SuppressLint("StaticFieldLeak")
object AndroidFileManagerApplication {

    var context: Context? = null
    var hasRoot = false
    var rankType = 0
    var isOpp = false
    private var fileSettingBean = FileSettingsBean()

    init {
        hasRoot = isRoot()
    }


    private fun isRoot(): Boolean {
        try {
            var process: Process = Runtime.getRuntime().exec("su")
            process.outputStream.write("exit\n".toByteArray())
            process.outputStream.flush()
            val i = process.waitFor()
            if (0 == i) {
                process = Runtime.getRuntime().exec("su")
                return true
            }
        } catch (e: Exception) {
            return false
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun init(context: Context){
        AndroidFileManagerApplication.context = context
        // 安装busybox
        if (!Busybox.systemBusyboxInstalled()) {
            ShellExecutor.setExtraEnvPath(
                FileWrite.getPrivateFilePath(context, context.getString(R.string.toolkit_install_path))
            )
        }

        Busybox(context).forceInstall {

        }
    }
}