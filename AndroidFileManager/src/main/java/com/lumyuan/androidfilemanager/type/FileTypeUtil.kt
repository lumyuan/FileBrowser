package com.lumyuan.androidfilemanager.type

import java.util.*

object FileTypeUtil {
    private val FILE_TYPE_MAP: MutableMap<String, Int> = HashMap()

    fun getType(url: String): Int {
        val fileType = url.substring(url.lastIndexOf(".") + 1, url.length)
        return FILE_TYPE_MAP[fileType.lowercase(Locale.getDefault())]
            ?: return -1
    }

    init {
        FILE_TYPE_MAP["bmp"] = 1
        FILE_TYPE_MAP["jpg"] = 1
        FILE_TYPE_MAP["jpeg"] = 1
        FILE_TYPE_MAP["png"] = 1
        FILE_TYPE_MAP["tiff"] = 6
        FILE_TYPE_MAP["gif"] = 1
        FILE_TYPE_MAP["pcx"] = 1
        FILE_TYPE_MAP["tga"] = 1
        FILE_TYPE_MAP["exif"] = 1
        FILE_TYPE_MAP["fpx"] = 1
        FILE_TYPE_MAP["svg"] = 1
        FILE_TYPE_MAP["psd"] = 1
        FILE_TYPE_MAP["cdr"] = 1
        FILE_TYPE_MAP["pcd"] = 1
        FILE_TYPE_MAP["dxf"] = 1
        FILE_TYPE_MAP["ufo"] = 1
        FILE_TYPE_MAP["eps"] = 1
        FILE_TYPE_MAP["ai"] = 1
        FILE_TYPE_MAP["raw"] = 1
        FILE_TYPE_MAP["wmf"] = 1
        FILE_TYPE_MAP["txt"] = 2
        FILE_TYPE_MAP["doc"] = 2
        FILE_TYPE_MAP["docx"] = 2
        FILE_TYPE_MAP["xls"] = 2
        FILE_TYPE_MAP["htm"] = 2
        FILE_TYPE_MAP["html"] = 2
        FILE_TYPE_MAP["jsp"] = 2
        FILE_TYPE_MAP["rtf"] = 2
        FILE_TYPE_MAP["wpd"] = 2
        FILE_TYPE_MAP["pdf"] = 2
        FILE_TYPE_MAP["ppt"] = 2
        FILE_TYPE_MAP["java"] = 2
        FILE_TYPE_MAP["mp4"] = 3
        FILE_TYPE_MAP["avi"] = 3
        FILE_TYPE_MAP["mov"] = 3
        FILE_TYPE_MAP["wmv"] = 3
        FILE_TYPE_MAP["asf"] = 3
        FILE_TYPE_MAP["navi"] = 3
        FILE_TYPE_MAP["3gp"] = 3
        FILE_TYPE_MAP["mkv"] = 3
        FILE_TYPE_MAP["f4v"] = 3
        FILE_TYPE_MAP["rmvb"] = 3
        FILE_TYPE_MAP["webm"] = 3
        FILE_TYPE_MAP["mp3"] = 4
        FILE_TYPE_MAP["wma"] = 4
        FILE_TYPE_MAP["wav"] = 4
        FILE_TYPE_MAP["mod"] = 4
        FILE_TYPE_MAP["ra"] = 4
        FILE_TYPE_MAP["cd"] = 4
        FILE_TYPE_MAP["md"] = 4
        FILE_TYPE_MAP["asf"] = 4
        FILE_TYPE_MAP["aac"] = 4
        FILE_TYPE_MAP["vqf"] = 4
        FILE_TYPE_MAP["ape"] = 4
        FILE_TYPE_MAP["mid"] = 4
        FILE_TYPE_MAP["ogg"] = 4
        FILE_TYPE_MAP["m4a"] = 4
        FILE_TYPE_MAP["vqf"] = 4
        FILE_TYPE_MAP["apk"] = 5
    }
}