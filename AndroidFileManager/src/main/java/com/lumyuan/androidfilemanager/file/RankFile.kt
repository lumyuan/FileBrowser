package com.lumyuan.androidfilemanager.file

import java.util.*

object RankFile {
    /**
     * 按文件名排序
     * @param filePath
     */
    fun orderByName(filePath: Array<String?>): Array<String?> {
        val fileRankList = ArrayList<String>()
        val files = listFiles(filePath)
        files.sortWith(Comparator sort@{ o1: File, o2: File ->
            if (o1.isDirectory() && o2.isFile()) return@sort -1
            if (o1.isFile() && o2.isDirectory()) return@sort 1
            o1.getPath().compareTo(o2.getPath())
        })
        for (file1 in files) {
            fileRankList.add(file1.getPath())
        }
        return fileRankList.toTypedArray()
    }

    /**
     * 按文件名排序
     * @param filePath
     */
    fun orderByType(filePath: Array<String?>): Array<String?> {
        val fileRankList = ArrayList<String>()
        val files = listFiles(filePath)
        files.sortWith(Comparator sort@{ o1: File, o2: File ->
            if (o1.isDirectory() && o2.isFile()) return@sort -1
            if (o1.isFile() && o2.isDirectory()) return@sort 1
            val n1 = o1.getName()
            val n2 = o2.getName()
            if (n1.contains(".") && n2.contains(".")) return@sort n1.substring(n1.lastIndexOf(".") + 1)
                .compareTo(n2.substring(n2.lastIndexOf(".") + 1))
            o1.getPath().compareTo(o2.getPath())
        })
        for (file1 in files) {
            fileRankList.add(file1.getPath())
        }
        return fileRankList.toTypedArray()
    }

    /**
     * 按文件修改时间排序
     * @param filePath
     */
    fun orderByDate(filePath: Array<String?>): Array<String?> {
        val fileRankList = ArrayList<String>()
        val files = listFiles(filePath)
        Arrays.sort(files, object : Comparator<File> {
            override fun compare(f1: File, f2: File): Int {
                val diff = f1.lastModified() - f2.lastModified()
                return if (diff > 0) 1 else if (diff == 0L) 0 else -1 // 如果 if 中修改为 返回-1 同时此处修改为返回 1 排序就会是递减
            }

            override fun equals(other: Any?): Boolean {
                return true
            }
        })
        for (file1 in files) {
            fileRankList.add(file1.getPath())
        }
        return fileRankList.toTypedArray()
    }

    /**
     * 按文件大小排序
     * @param filePath
     */
    fun orderBySize(filePath: Array<String?>): Array<String?
            > {
        val fileRankList = ArrayList<String>()
        val files = listFiles(filePath)
        val fileList = listOf(*files)
        Collections.sort(fileList, object : Comparator<File> {
            override fun compare(f1: File, f2: File): Int {
                val s1 = f1.length()
                val s2 = f2.length()
                val diff = s1 - s2
                return if (diff > 0) 1 else if (diff == 0L) 0 else -1 // 如果 if 中修改为 返回-1 同时此处修改为返回 1 排序就会是递减
            }

            override fun equals(obj: Any?): Boolean {
                return true
            }
        })
        for (file1 in files) {
            fileRankList.add(file1.getPath())
        }
        return fileRankList.toTypedArray()
    }

    private fun listFiles(arr: Array<String?>): Array<File> {
        val list: MutableList<File> = ArrayList()
        for (s in arr) {
            list.add(File(s!!))
        }
        return list.toTypedArray()
    }
}