package com.lumyuan.androidfilemanager.base

import java.io.InputStream
import java.io.OutputStream

interface SimpleStream {
    fun openInputStream(): InputStream
    fun openOutputStream(): OutputStream
}