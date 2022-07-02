package com.lumyuan.androidfilemanager.utils

class ShellHandler(val isRoot: Boolean) {

    interface RuntimeListener {
        fun onMessage(msg: String)
        fun onError(error: String)
    }

    private val runtimeListeners: ArrayList<RuntimeListener> = ArrayList()

    private fun getCmd(isRoot: Boolean): String{
        return if (isRoot) "su\n" else "sh\n"
    }

    fun asyncCmd(cmd: String){
        val exec = Runtime.getRuntime().exec(getCmd(isRoot))
        val bufferedWriter = exec.outputStream.bufferedWriter()
        val messageReader = exec.inputStream.bufferedReader()
        val errorReader = exec.errorStream.bufferedReader()

        bufferedWriter.write("$cmd\n")
        bufferedWriter.flush()

        var msgLine: String?
        var errLine: String?

        Thread{
            while (run {
                    msgLine = messageReader.readLine()
                    msgLine
                } != null) {
                for (runtimeListener in runtimeListeners) {
                    runtimeListener.onMessage(msgLine!!)
                }
            }
        }.start()

        Thread{
            while (run {
                    errLine = errorReader.readLine()
                    errLine
                } != null){
                for (runtimeListener in runtimeListeners) {
                    runtimeListener.onError(errLine!!)
                }
            }
        }.start()

    }

    fun addRuntimeListener(runtimeListener: RuntimeListener){
        runtimeListeners.add(runtimeListener)
    }

    fun removeAllRuntimeListener(){
        runtimeListeners.clear()
    }
}