package com.lumyuan.androidfilemanager

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.lumyuan.androidfilemanager.dao.FIleOrderRules
import com.lumyuan.androidfilemanager.dao.FileSettingsBean
import com.lumyuan.androidfilemanager.utils.Busybox
import com.lumyuan.androidfilemanager.utils.FileWrite
import com.lumyuan.androidfilemanager.utils.ShellExecutor
import java.io.File

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
    private var orderRulesPath: String? = null
    private val gson = Gson()
    private fun getOrderRulesPath(context: Context): String{
        if (orderRulesPath == null)
            orderRulesPath = context.filesDir.absolutePath + "/cache/order inverse/order.json"
        return orderRulesPath!!
    }

    fun getOrderRules():Int{
        return if (File(getOrderRulesPath(context!!)).exists()) {
            val json = FileUtils.readText(getOrderRulesPath(context!!))
            val bean = gson.fromJson(json, FileSettingsBean::class.java)
            fileSettingBean = bean
            fileSettingBean.inverseOrder + fileSettingBean.order
        }else {
            val bean =
                FileSettingsBean(FIleOrderRules.ORDER_BY_NAME, FIleOrderRules.ORDER_BY_SIMPLE)
            fileSettingBean = bean
            saveOrderRules(fileSettingBean.inverseOrder + fileSettingBean.order)
            fileSettingBean.inverseOrder + fileSettingBean.order
        }
    }

    fun saveOrderRules(inverse: Int){
        val bean: FileSettingsBean
        when(inverse){
            0 ->{
                bean = FileSettingsBean(0, 0)
            }
            1->{
                bean = FileSettingsBean(1, 0)
            }
            2->{
                bean = FileSettingsBean(2, 0)
            }
            3->{
                bean = FileSettingsBean(3, 0)
            }
            4->{
                bean = FileSettingsBean(0, 4)
            }
            5->{
                bean = FileSettingsBean(1, 4)
            }
            6->{
                bean = FileSettingsBean(2, 4)
            }
            7->{
                bean = FileSettingsBean(3, 4)
            }
            8->{
                bean = FileSettingsBean(4, 4)
            }
            else ->{
                bean = FileSettingsBean(0, 0)
            }
        }
        fileSettingBean = bean
        val json = gson.toJson(fileSettingBean)
        val path = getOrderRulesPath(context!!)
        if (!File(path).exists()){
            File(path).parentFile?.mkdirs()
            File(path).createNewFile()
        }
        FileUtils.writeText(path, json)
    }
}