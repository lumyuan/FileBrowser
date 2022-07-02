package com.lumyuan.filebrowser.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.gyf.barlibrary.ImmersionBar
import com.lumyuan.androidfilemanager.utils.ExternalStoragePermissionsUtils

open class BaseActivity: AppCompatActivity() {

    inline fun <VB : ViewBinding> AppCompatActivity.binding(
        crossinline inflate: (LayoutInflater) -> VB
    ) = lazy{
        inflate(layoutInflater).apply {
            setContentView(this.root)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //全面屏适配
        ImmersionBar.with(this)
            .transparentStatusBar()
            .statusBarDarkFont(true)
            .transparentNavigationBar()
            .navigationBarDarkIcon(true)
            .transparentNavigationBar()
            .init()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        ExternalStoragePermissionsUtils.saveExternalStoragePermission(this, permissions)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ExternalStoragePermissionsUtils.saveApplicationExclusiveDirectoryPermission(this, requestCode, data)
    }
}