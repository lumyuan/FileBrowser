package com.lumyuan.filebrowser.pojo

import android.graphics.drawable.Drawable

class FileItemBean {
    var isDirectory = true
    var name = ""
    var path = ""
    var length = 0L
    var lastModified = 0L
    var appIcon: Drawable? = null
    override fun toString(): String {
        return "{isDirectory:$isDirectory, name:\"$name\", path: \"$path\", length:$length, lastModified:$lastModified, appIcon:${appIcon.toString()}}"
    }
}