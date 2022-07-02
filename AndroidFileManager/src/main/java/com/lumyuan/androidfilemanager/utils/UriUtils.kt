package com.lumyuan.androidfilemanager.utils

import android.net.Uri
import android.os.Environment

object UriUtils {
    private val rootPath = Environment.getExternalStorageDirectory().path
    fun path2Uri(path: String): Uri {
        val paths =
            path.replace("/storage/emulated/0/Android/data".toRegex(), "").split("/").toTypedArray()
        val stringBuilder =
            StringBuilder("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3AAndroid%2Fdata")
        for (p in paths) {
            if (p.isEmpty()) continue
            stringBuilder.append("%2F").append(p)
        }
        return Uri.parse(stringBuilder.toString())
    }

    fun uri2Path(uri: String): String {
        val substring = uri.substring(0, uri.lastIndexOf("%3A"))
        val replaceAll = uri.replace(substring.toRegex(), "")
        return rootPath + replaceAll.replace("%2F".toRegex(), "/").replace("%3A".toRegex(), "/")
    }
}