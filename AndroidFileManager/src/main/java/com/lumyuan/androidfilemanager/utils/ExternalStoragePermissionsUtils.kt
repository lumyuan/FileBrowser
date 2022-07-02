package com.lumyuan.androidfilemanager.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile

object ExternalStoragePermissionsUtils {
    private val mPermissionList: ArrayList<String> = ArrayList()
    private val permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    fun getExternalStoragePermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= 23) { //6.0才用动态权限
            //逐个判断你要的权限是否已经通过
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(
                        activity,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    mPermissionList.add(permission) //添加还未授予的权限
                }
            }
            if (mPermissionList.size > 0) { //有权限没有通过，需要申请
                val mRequestCode = 100
                ActivityCompat.requestPermissions(activity, permissions, mRequestCode)
            }
        }
    }

    fun hasExternalStoragePermission(activity: Activity): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) { //6.0才用动态权限
            //逐个判断你要的权限是否已经通过
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(
                        activity,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    mPermissionList.add(permission) //添加还未授予的权限
                }
            }
            mPermissionList.size <= 0
        } else {
            true
        }
    }

    fun getAllFilesAccessPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                activity.startActivity(intent)
            }
        }
    }

    fun hasAllFilesAccessPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            true
        }
    }

    fun getApplicationExclusiveDirectoryPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !hasApplicationExclusiveDirectoryPermission(activity)) {
            val uri =
                Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata")
            val documentFile = DocumentFile.fromTreeUri(activity, uri)
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            intent.flags = (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                    or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
            assert(documentFile != null)
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, documentFile!!.uri)
            activity.startActivityForResult(intent, 11)
        }
    }

    fun hasApplicationExclusiveDirectoryPermission(activity: Activity): Boolean {
        return if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            return hasPermissions(activity)
        }else{
            true
        }
    }

    private fun hasPermissions(activity: Activity): Boolean {
        for (persistedUriPermission in activity.contentResolver.persistedUriPermissions) {
            if (persistedUriPermission.isReadPermission && persistedUriPermission.uri.toString() == "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata") {
                return true
            }
        }
        return false
    }

    fun saveExternalStoragePermission(activity: Activity, permissions: Array<out String>){
        mPermissionList.clear()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                mPermissionList.add(permission) //添加还未授予的权限
            }
        }
    }

    @SuppressLint("WrongConstant")
    fun saveApplicationExclusiveDirectoryPermission(activity: Activity, requestCode: Int, data: Intent?){
        if (!hasApplicationExclusiveDirectoryPermission(activity)){
            if (data != null){
                val uri = data.data
                if (requestCode == 11 && uri != null) {
                    activity.contentResolver.takePersistableUriPermission(
                        uri, data.flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION
                                or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    )
                }
            }
        }
    }
}