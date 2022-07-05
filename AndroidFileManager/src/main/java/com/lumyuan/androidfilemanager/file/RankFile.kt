package com.lumyuan.androidfilemanager.file

import com.lumyuan.androidfilemanager.dao.FIleOrderRules
import java.util.*

object RankFile {
    /**
     * 按文件名排序
     * @param filePath
     */
    fun orderByName(filePath: Array<File?>, rules: Int){
        if (rules == 0){
            filePath.sortWith(Comparator sort@{ o1: File?, o2: File? ->
                if (o1!!.isDirectory() && o2!!.isFile()) return@sort -1
                if (o1.isFile() && o2!!.isDirectory()) return@sort 1
                return@sort o1.getName().compareTo(o2!!.getName())
            })
        }else {
            filePath.sortWith(Comparator sort@{ o1: File?, o2: File? ->
                if (o1!!.isDirectory() && o2!!.isFile()) return@sort -1
                if (o1.isFile() && o2!!.isDirectory()) return@sort 1
                return@sort -o1.getName().compareTo(o2!!.getName())
            })
        }
    }

    /**
     * 按文件修改时间排序
     * @param filePath
     */
    fun orderByDate(filePath: Array<File?>, rules: Int){
        if (rules == 2){
            filePath.sortWith(Comparator sort@{ o1: File?, o2: File? ->
                if (o1!!.isDirectory() && o2!!.isFile()) return@sort -1
                if (o1.isFile() && o2!!.isDirectory()) return@sort 1
                return@sort if (o1.lastModified() == o2!!.lastModified()){
                    o1.getName().compareTo(o2.getName())
                }else if (o1.lastModified() > o2.lastModified()){
                    1
                }else {
                    -1
                }
            })
        }else {
            filePath.sortWith(Comparator sort@{ o1: File?, o2: File? ->
                if (o1!!.isDirectory() && o2!!.isFile()) return@sort -1
                if (o1.isFile() && o2!!.isDirectory()) return@sort 1
                return@sort -if (o1.lastModified() == o2!!.lastModified()){
                    o1.getName().compareTo(o2.getName())
                }else if (o1.lastModified() > o2.lastModified()){
                    1
                }else {
                    -1
                }
            })
        }
    }

    /**
     * 按文件大小排序
     * @param filePath
     */
    fun orderBySize(filePath: Array<File?>, rules: Int) {
        if (rules == 1){
            filePath.sortWith(Comparator sort@{ o1: File?, o2: File? ->
                if (o1!!.isDirectory() && o2!!.isFile()) return@sort -1
                if (o1.isFile() && o2!!.isDirectory()) return@sort 1
                return@sort if (o1.isFile() && o2!!.isFile()){
                    if (o1.length() == o2.length()){
                        o1.getName().compareTo(o2.getName())
                    }else if (o1.length() > o2.length()){
                        1
                    }else {
                        -1
                    }
                }else{
                    o1.getName().compareTo(o2!!.getName())
                }
            })
        }else {
            filePath.sortWith(Comparator sort@{ o1: File?, o2: File? ->
                if (o1!!.isDirectory() && o2!!.isFile()) return@sort -1
                if (o1.isFile() && o2!!.isDirectory()) return@sort 1
                return@sort -if (o1.isFile() && o2!!.isFile()){
                    if (o1.length() == o2.length()){
                        o1.getName().compareTo(o2.getName())
                    }else if (o1.length() > o2.length()){
                        1
                    }else {
                        -1
                    }
                }else{
                    o1.getName().compareTo(o2!!.getName())
                }
            })
        }
    }

    /**
     * 按文件类型排序
     * @param filePath
     */
    fun orderByType(filePath: Array<File?>, rules: Int) {
        if (rules == 3){
            filePath.sortWith(Comparator sort@{ o1: File?, o2: File? ->
                if (o1!!.isDirectory() && o2!!.isFile()) return@sort -1
                if (o1.isFile() && o2!!.isDirectory()) return@sort 1
                val n1 = o1.getName()
                val n2 = o2!!.getName()
                if (o1.isFile() && o2.isFile()){
                    try{
                        val t1 = n1.substring(n1.lastIndexOf(".") + 1)
                        val t2 = n2.substring(n2.lastIndexOf(".") + 1)
                        return@sort t1.compareTo(t2)
                    }catch (e: Exception){
                        return@sort if (n1.endsWith(".") && !n2.endsWith(".")){
                            -1
                        }else {
                            1
                        }
                    }
                }
                return@sort n1.compareTo(n2)
            })
        }else {
            filePath.sortWith(Comparator sort@{ o1: File?, o2: File? ->
                if (o1!!.isDirectory() && o2!!.isFile()) return@sort -1
                if (o1.isFile() && o2!!.isDirectory()) return@sort 1
                val n1 = o1.getName()
                val n2 = o2!!.getName()
                if (o1.isFile() && o2.isFile()){
                    try{
                        val t1 = n1.substring(n1.lastIndexOf(".") + 1)
                        val t2 = n2.substring(n2.lastIndexOf(".") + 1)
                        return@sort -t1.compareTo(t2)
                    }catch (e: Exception){
                        return@sort -if(n1.endsWith(".") && !n2.endsWith(".")){
                            -1
                        }else {
                            1
                        }
                    }
                }
                return@sort -n1.compareTo(n2)
            })
        }
    }
}