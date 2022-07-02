package com.lumyuan.androidfilemanager.base

import java.io.File

class SimpleExternalStorageFile(path: String) : SimpleFile {
    private var file: File

    override fun exists(): Boolean {
        return file.exists()
    }

    override fun getName(): String {
        return file.name
    }

    override fun getParent(): String {
        return file.parent!!
    }

    override fun getPath(): String {
        return file.path;
    }

    override fun canRead(): Boolean {
        return file.canRead()
    }

    override fun canWrite(): Boolean {
        return file.canWrite()
    }

    override fun isDirectory(): Boolean {
        return file.isDirectory
    }

    override fun isFile(): Boolean {
        return file.isFile
    }

    override fun lastModified(): Long {
        return file.lastModified()
    }

    override fun length(): Long {
        return file.length()
    }

    override fun createNewFile(): Boolean {
        return file.createNewFile()
    }

    override fun delete(): Boolean {
        return file.delete()
    }

    override fun list(): Array<String?> {
        val list = arrayOfNulls<String>(file.list()!!.size)
        val fileList = file.listFiles()
        if (fileList != null) {
            for (i in fileList.indices) {
                list[i] = fileList[i].path
            }
        }
        return list
    }

    override fun mkDirs(): Boolean {
        return file.mkdirs()
    }

    override fun renameTo(name: String): Boolean {
        return file.renameTo(File(file.parent!! + "/" + name))
    }

    init {
        this.file = File(path)
    }
}