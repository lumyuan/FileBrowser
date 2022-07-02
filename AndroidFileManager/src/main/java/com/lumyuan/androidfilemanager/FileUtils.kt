package com.lumyuan.androidfilemanager

import com.lumyuan.androidfilemanager.file.File

class FileUtils {
    companion object{
        fun readBytes(path: String): ByteArray{
            val openInputStream = File(path).openInputStream()
            val readBytes = openInputStream.readBytes()
            openInputStream.close()
            return readBytes
        }

        fun writeBytes(path: String, bytes: ByteArray): Boolean{
            return try {
                val openOutputStream = File(path).openOutputStream()
                openOutputStream.write(bytes)
                openOutputStream.close()
                true
            }catch (e: Exception){
                false
            }
        }

        fun readText(path: String): String{
            return String(readBytes(path))
        }

        fun writeText(path: String, content: String): Boolean{
            return writeBytes(path, content.toByteArray())
        }

        fun copyFile(oldPath: String, newPath: String): Boolean{
            val readBytes = readBytes(oldPath)
            return writeBytes(newPath, readBytes)
        }

        fun cutFile(oldPath: String, newPath: String): Boolean{
            val readBytes = readBytes(oldPath)
            val writeBytes = writeBytes(newPath, readBytes)
            File(oldPath).delete()
            return writeBytes;
        }
    }
}