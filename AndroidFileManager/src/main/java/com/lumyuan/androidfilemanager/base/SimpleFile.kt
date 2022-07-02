package com.lumyuan.androidfilemanager.base


interface SimpleFile {

    fun exists(): Boolean

    fun getName(): String

    fun getParent(): String

    fun getPath(): String

    fun canRead(): Boolean

    fun canWrite(): Boolean

    fun isDirectory(): Boolean

    fun isFile(): Boolean

    fun lastModified(): Long

    fun length(): Long

    fun createNewFile(): Boolean

    fun delete(): Boolean

    fun list(): Array<String?>

    fun mkDirs(): Boolean

    fun renameTo(name: String): Boolean
}