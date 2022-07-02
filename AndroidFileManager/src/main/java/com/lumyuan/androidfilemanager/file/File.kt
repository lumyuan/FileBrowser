package com.lumyuan.androidfilemanager.file

import android.os.Build
import android.os.Environment
import com.lumyuan.androidfilemanager.AndroidFileManagerApplication
import com.lumyuan.androidfilemanager.base.*
import com.lumyuan.androidfilemanager.utils.UriUtils.path2Uri
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class File(private val path: String) : SimpleStream, SimpleFile{

    private lateinit var simpleDocumentFile: SimpleDocumentFile
    private var simpleFile: SimpleExternalStorageFile = SimpleExternalStorageFile(path)
    private val context = AndroidFileManagerApplication.context
    private lateinit var simpleRootFile: SimpleRootFile
    private val appDir = AndroidFileManagerApplication.context!!.filesDir.absolutePath + "/temp stream/"

    private var isPreviewDir = false
    private var isRootDir = false
    init {
        if (isRootDir()) simpleRootFile = SimpleRootFile(path)
        if (isPreviewDir()) simpleDocumentFile = SimpleDocumentFile(path)
        isPreviewDir = isPreviewDir()
        isRootDir = isRootDir()
    }

    fun isPreviewDir(): Boolean{
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                && path.startsWith(Environment.getExternalStorageDirectory().path + "/Android/data")
    }

    fun isRootDir(): Boolean{
        return if (AndroidFileManagerApplication.hasRoot){
            !path.startsWith(Environment.getExternalStorageDirectory().path)
        }else{
            false
        }
    }

    override fun exists(): Boolean {
        return if (isPreviewDir){
            simpleDocumentFile.exists()
        }else if (isRootDir){
            simpleRootFile.exists()
        } else {
            simpleFile.exists()
        }
    }

    override fun getName(): String {
        return if (isPreviewDir){
            simpleDocumentFile.getName()
        }else if (isRootDir){
            simpleRootFile.getName()
        } else {
            simpleFile.getName()
        }
    }

    override fun getParent(): String {
        return if (isPreviewDir){
            simpleDocumentFile.getParent()
        }else if (isRootDir){
            simpleRootFile.getParent()
        } else {
            simpleFile.getParent()
        }
    }

    override fun getPath(): String {
        return if (isPreviewDir){
            simpleDocumentFile.getPath()
        }else if (isRootDir){
            simpleRootFile.getPath()
        } else {
            simpleFile.getPath()
        }
    }

    override fun canRead(): Boolean {
        return if (isPreviewDir){
            simpleDocumentFile.canRead()
        }else if (isRootDir){
            simpleRootFile.canRead()
        } else {
            simpleFile.canRead()
        }
    }

    override fun canWrite(): Boolean {
        return if (isPreviewDir){
            simpleDocumentFile.canWrite()
        }else if (isRootDir){
            simpleRootFile.canWrite()
        } else {
            simpleFile.canWrite()
        }
    }

    override fun isDirectory(): Boolean {
        return if (isPreviewDir){
            simpleDocumentFile.isDirectory()
        }else if (isRootDir){
            simpleRootFile.isDirectory()
        } else {
            simpleFile.isDirectory()
        }
    }

    override fun isFile(): Boolean {
        return if (isPreviewDir){
            simpleDocumentFile.isFile()
        }else if (isRootDir){
            simpleRootFile.isFile()
        } else {
            simpleFile.isFile()
        }
    }

    override fun lastModified(): Long {
        return if (isPreviewDir){
            simpleDocumentFile.lastModified()
        }else if (isRootDir){
            simpleRootFile.lastModified()
        } else {
            simpleFile.lastModified()
        }
    }

    override fun length(): Long {
        return if (isPreviewDir){
            simpleDocumentFile.length()
        }else if (isRootDir){
            simpleRootFile.length()
        } else {
            simpleFile.length()
        }
    }

    override fun createNewFile(): Boolean {
        return if (isPreviewDir){
            simpleDocumentFile.createNewFile()
        }else if (isRootDir){
            simpleRootFile.createNewFile()
        } else {
            simpleFile.createNewFile()
        }
    }

    override fun delete(): Boolean {
        return if (isPreviewDir){
            simpleDocumentFile.delete()
        }else if (isRootDir){
            simpleRootFile.delete()
        } else {
            simpleFile.delete()
        }
    }

    override fun list(): Array<String?> {
        var list = if (isPreviewDir){
            simpleDocumentFile.list()
        }else if (isRootDir){
            simpleRootFile.list()
        } else {
            simpleFile.list()
        }
        list = RankFile.orderByName(list)
        return list
    }

    fun listNoRank(): Array<String?> {
        return if (isPreviewDir){
            simpleDocumentFile.list()
        }else if (isRootDir){
            simpleRootFile.list()
        } else {
            simpleFile.list()
        }
    }

    override fun mkDirs(): Boolean {
        return if (isPreviewDir){
            simpleDocumentFile.mkDirs()
        }else if (isRootDir){
            simpleRootFile.mkDirs()
        } else {
            simpleFile.mkDirs()
        }
    }

    override fun renameTo(name: String): Boolean {
        return if (isPreviewDir){
            simpleDocumentFile.renameTo(name)
        }else if (isRootDir){
            simpleRootFile.renameTo(name)
        } else {
            simpleFile.renameTo(name)
        }
    }

    @Deprecated("该方法无法打开非sdcard目录的输入流")
    override fun openInputStream(): InputStream {
        return if (isPreviewDir){
            context?.contentResolver?.openInputStream(path2Uri(path))!!
        } else {
            FileInputStream(path)
        }
    }

    @Deprecated("该方法无法打开非sdcard目录的输出流")
    override fun openOutputStream(): OutputStream {
        return if (isPreviewDir){
            if (!simpleDocumentFile.exists()) simpleDocumentFile.createNewFile()
            context?.contentResolver?.openOutputStream(path2Uri(path), "rwt")!!
        } else {
            FileOutputStream(path)
        }
    }
}