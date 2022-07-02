package com.lumyuan.androidfilemanager.utils

import java.io.File

class RootFileInfo {
    constructor()
    constructor(path: String) {
        val file = RootFile.fileInfo(path)
        if (file != null) {
            this.parentDir = file.parentDir
            this.filePath = file.filePath
            this.isDirectory = file.isDirectory
        }
    }

    var parentDir: String = ""
    var filePath: String = ""
    var isDirectory: Boolean = false
    var fileSize: Long = 0;
    var lastModified: Long = 0

    val fileName: String
        get() {
            if (filePath.endsWith("/")) {
                return filePath.substring(0, filePath.length - 1)
            }
            return filePath
        }

    val absolutePath: String
        get() = "$parentDir/$fileName"


    public fun exists(): Boolean {
        return RootFile.itemExists(this.absolutePath)
    }

    public fun isFile(): Boolean {
        return !isDirectory
    }

    public fun getParent(): String {
        return this.parentDir
    }

    public fun getName(): String {
        return this.fileName
    }

    public fun listFiles(): Array<String?> {
        return RootFile.list(this.absolutePath)
    }

    fun length(): Long {
        return this.fileSize
    }
}
