package com.lumyuan.androidfilemanager

import android.content.Context
import android.os.Handler
import com.lumyuan.androidfilemanager.utils.KeepShellAsync
import com.lumyuan.androidfilemanager.utils.ShellHandler
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun asyncShellTest(){
        val shellHandler = ShellHandler(true)
        shellHandler.addRuntimeListener(object : ShellHandler.RuntimeListener{
            override fun onMessage(msg: String) {
                //TODO 这里是日志

            }

            override fun onError(error: String) {

            }
        })
        shellHandler.asyncCmd("echo 这里输入指令")
    }
}