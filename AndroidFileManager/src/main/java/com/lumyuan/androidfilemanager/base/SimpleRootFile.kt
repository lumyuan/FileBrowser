package com.lumyuan.androidfilemanager.base

import com.lumyuan.androidfilemanager.utils.KeepShellPublic
import com.lumyuan.androidfilemanager.utils.RootFile
import com.lumyuan.androidfilemanager.utils.RootFileInfo
import java.io.File

class SimpleRootFile(private val path: String) : SimpleFile{

    private val rootFileInfo: RootFileInfo = RootFile.fileInfo(path)!!
    private val shell = KeepShellPublic.getInstance("file", true)
    private val file = File(path)
    override fun exists(): Boolean {
        return if (isDirectory()) RootFile.dirExists(path)
        else RootFile.fileExists(path)
    }

    override fun getName(): String {
        return rootFileInfo.fileName
    }

    override fun getParent(): String {
        return rootFileInfo.parentDir
    }

    override fun getPath(): String {
        return path
    }

    override fun canRead(): Boolean {
        return file.canRead()
    }

    override fun canWrite(): Boolean {
        return file.canWrite()
    }

    override fun isDirectory(): Boolean {
        return rootFileInfo.isDirectory
    }

    override fun isFile(): Boolean {
        return rootFileInfo.isFile()
    }

    override fun lastModified(): Long {
        /*val cmd = "FILE='$path'\n" +
                "last_time=\$(stat -c %Y \$FILE)\n" +
                "echo \$last_time\n"
        return try {
            val doCmdSync = shell.doCmdSync(cmd)
            java.lang.Long.parseLong(doCmdSync)
        }catch (e: Exception){
            0
        }*/
        return rootFileInfo.lastModified
    }

    override fun length(): Long {
        return if (isFile()) rootFileInfo.length() else list().size.toLong()
    }

    override fun createNewFile(): Boolean {
        shell.doCmdSync("echo \"\\c\" > $path")
        return exists()
    }

    override fun delete(): Boolean {
        RootFile.deleteDirOrFile(path)
        return true
    }

    override fun list(): Array<String?> {
        return RootFile.list(path)
    }

    override fun mkDirs(): Boolean {
        shell.doCmdSync("touch $path")
        return exists()
    }

    override fun renameTo(name: String): Boolean {
        val newFile = "${rootFileInfo.parentDir}/$name"
        shell.doCmdSync("mv $path $newFile")
        return SimpleRootFile(newFile).exists()
    }

}