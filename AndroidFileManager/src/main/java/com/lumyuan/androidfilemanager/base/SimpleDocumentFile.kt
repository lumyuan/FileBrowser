package com.lumyuan.androidfilemanager.base

import android.content.Context
import android.os.Environment
import androidx.documentfile.provider.DocumentFile
import com.lumyuan.androidfilemanager.AndroidFileManagerApplication
import com.lumyuan.androidfilemanager.utils.UriUtils.path2Uri
import com.lumyuan.androidfilemanager.utils.UriUtils.uri2Path
import java.io.File

class SimpleDocumentFile(private val path: String) : SimpleFile {
    private var documentFile: DocumentFile
    private var file: File
    private val context: Context = AndroidFileManagerApplication.context!!
    private val rootPath = Environment.getExternalStorageDirectory().path

    init {
        documentFile = DocumentFile.fromTreeUri(context, path2Uri(path))!!
        file = File(path)
    }

    override fun exists(): Boolean {
        return documentFile.exists()
    }

    override fun getName(): String {
        return file.name
    }

    override fun getParent(): String {
        return file.parent!!
    }

    override fun getPath(): String {
        return this.path
    }

    override fun canRead(): Boolean {
        return documentFile.canRead()
    }

    override fun canWrite(): Boolean {
        return documentFile.canWrite()
    }

    override fun isDirectory(): Boolean {
        return documentFile.isDirectory
    }

    override fun isFile(): Boolean {
        return documentFile.isFile
    }

    override fun lastModified(): Long {
        return documentFile.lastModified()
    }

    override fun length(): Long {
        return documentFile.length()
    }

    override fun createNewFile(): Boolean {
        val patent = DocumentFile.fromTreeUri(context, path2Uri(getParent()))
        if (patent != null && !patent.exists()) SimpleDocumentFile(getParent()).mkDirs()
        if (documentFile.exists()) return false
        if (documentFile.isDirectory) return false
        val name = File(getPath()).name
        SimpleDocumentFile(getParent()).mkDirs()
        val parentFile = DocumentFile.fromTreeUri(context, path2Uri(getParent())) ?: return false
        val file = parentFile.createFile("*/*", name) ?: return false
        return file.exists()
    }

    override fun delete(): Boolean {
        return documentFile.delete()
    }

    override fun list(): Array<String?> {
        val documentFiles = documentFile.listFiles()
        val strings = arrayOfNulls<String>(documentFiles.size)
        for (i in documentFiles.indices) {
            strings[i] = uri2Path(documentFiles[i].uri.toString())
        }
        return strings
    }

    override fun mkDirs(): Boolean {
        val dataPath = StringBuilder("$rootPath/Android/data")
        var uri2Path = uri2Path(documentFile.uri.toString())
        uri2Path = uri2Path.replace(rootPath + "/Android/data/".toRegex(), "")
        val split = uri2Path.split("/").toTypedArray()
        var dir: DocumentFile? = null
        for (s in split) {
            dataPath.append("/").append(s)
            val documentFile = DocumentFile.fromTreeUri(
                context,
                path2Uri(dataPath.toString())
            )!!
            if (!documentFile.exists()) {
                dir = DocumentFile.fromTreeUri(
                    context,
                    path2Uri(File(dataPath.toString()).parent!!)
                )?.createDirectory(s)
            }
        }
        return dir != null && dir.exists()
    }

    override fun renameTo(name: String): Boolean {
        return documentFile.renameTo(name.substring(name.lastIndexOf("/") + 1))
    }

}