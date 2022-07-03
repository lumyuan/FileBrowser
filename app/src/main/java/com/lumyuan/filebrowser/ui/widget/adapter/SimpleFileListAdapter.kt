package com.lumyuan.filebrowser.ui.widget.adapter

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Environment
import android.os.Handler
import android.text.format.Formatter
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.lumyuan.androidfilemanager.FileUtils
import com.lumyuan.androidfilemanager.file.File
import com.lumyuan.androidfilemanager.file.RankFile
import com.lumyuan.filebrowser.FileBrowserApplication
import com.lumyuan.filebrowser.R
import com.lumyuan.filebrowser.databinding.ItemSimpleFileBinding
import com.lumyuan.filebrowser.model.FileChangedViewModel
import com.lumyuan.filebrowser.pojo.FileItemBean
import com.lumyuan.filebrowser.pojo.SimpleFileBean
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import java.io.*
import kotlin.collections.HashMap


class SimpleFileListAdapter(private var activity: AppCompatActivity,
                            private val recyclerView: RecyclerView) : RecyclerView.Adapter<SimpleFileListAdapter.FileHolder>(){

    private val viewModel: FileChangedViewModel = ViewModelProvider(activity).get(FileChangedViewModel::class.java)
    private val list = ArrayList<Map<Int, Any>>()
    @SuppressLint("SimpleDateFormat")
    private val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileHolder {
        return FileHolder(ItemSimpleFileBinding.bind(View.inflate(activity, R.layout.item_simple_file, null)))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FileHolder, position: Int) {
        val item = list[position]
        val binding = holder.binding
        //加载布局
        when(item[1]){
            "loading..." ->{
                binding.fileItem.visibility = View.GONE
                binding.loadItem.visibility = View.VISIBLE
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                binding.root.layoutParams = layoutParams
                binding.listLoad.visibility = View.VISIBLE
                binding.loadText.text = "全力加载中，请稍候..."
                setAlpha(binding.root, 0F, 1F, 400)
            }
            "field" -> {
                binding.fileItem.visibility = View.GONE
                binding.loadItem.visibility = View.VISIBLE
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                binding.root.layoutParams = layoutParams
                binding.listLoad.visibility = View.GONE
                binding.loadText.text = "列表读取失败了..."
                setAlpha(binding.root, 0F, 1F, 400)
            }
            "none" -> {
                binding.fileItem.visibility = View.GONE
                binding.loadItem.visibility = View.VISIBLE
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                binding.root.layoutParams = layoutParams
                binding.listLoad.visibility = View.GONE
                binding.loadText.text = "这里什么也没有..."
                setAlpha(binding.root, 0F, 1F, 400)
            }
            else ->{
                val path = item[0] as File?
                binding.fileItem.visibility = View.VISIBLE
                binding.loadItem.visibility = View.GONE
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                binding.root.layoutParams = layoutParams
                loaded(binding, false)
                val bean = FileItemBean()
                Observable.create<FileItemBean> {
                    try {
                        bean.name = path!!.getName()
                        bean.isDirectory = path.isDirectory()
                        bean.path = path.getPath()
                        bean.lastModified = path.lastModified()
                    }catch (e: Exception){}
                    it.onNext(bean)
                    it.onComplete()
                }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        binding.fileName.text = it.name
                        binding.fileConfig.text = if (bean.isDirectory)
                            "...项\t\t|\t\t${simpleDateFormat.format(Date(bean.lastModified))}"
                        else
                            "${Formatter.formatFileSize(activity, bean.length).replace("吉字节", "GB")}\t\t|\t\t${simpleDateFormat.format(Date(bean.lastModified))}"
                        Observable.create<Any> { config ->
                            bean.length = if (bean.isDirectory) try {
                                path!!.list().size
                            }catch (e: Exception){
                                0
                            }.toLong() else path!!.length()
                            config.onNext("")
                            config.onComplete()
                        }.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                binding.fileConfig.text = if (bean.isDirectory)
                                    "${bean.length}项\t\t|\t\t${simpleDateFormat.format(Date(bean.lastModified))}"
                                else
                                    "${Formatter.formatFileSize(activity, bean.length).replace("吉字节", "GB")}\t\t|\t\t${simpleDateFormat.format(Date(bean.lastModified))}"
                            }
                        if (bean.isDirectory){
                            binding.fileRightIcon.visibility = View.VISIBLE
                            binding.fileIcon.setImageResource(R.mipmap.ic_folder)
                            binding.appIcon.visibility = View.GONE
                            Observable.create { draw ->
                                try {
                                    val findAppIcon = FileBrowserApplication.appList[bean.name.trim()]!!.icon
                                    draw.onNext(findAppIcon)
                                }catch (e: Exception){
                                    e.printStackTrace()
                                }
                                draw.onComplete()
                            }.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe { draw ->
                                    binding.appIcon.setImageDrawable(draw)
                                    binding.appIcon.visibility = View.VISIBLE
                                }
                            binding.fileTipLayout.visibility = View.GONE
                            try {
                                val findAppName = FileBrowserApplication.appList[bean.name.trim()]!!.name
                                binding.fileTip.text = findAppName
                                binding.fileTipLayout.visibility = View.VISIBLE
                            }catch (e: Exception){
                                e.printStackTrace()
                            }
                        }else {
                            binding.fileRightIcon.visibility = View.GONE
                            setIcon(binding, bean.name, path!!.getPath())
                            binding.fileTipLayout.visibility = View.GONE
                            binding.appIcon.visibility = View.GONE

                        }

                        binding.root.setOnClickListener {
                            if (bean.isDirectory){
                                openDirectory(bean.name)
                            }else {

                            }
                        }
                        loaded(binding, true)
                    }

            }
        }
    }

    private fun loaded(binding: ItemSimpleFileBinding, isShow: Boolean){
        if (isShow){
            binding.loadView.visibility = View.INVISIBLE
            binding.configLayout.visibility = View.VISIBLE
            binding.rightLayout.visibility = View.VISIBLE
            binding.fileIcon.visibility = View.VISIBLE
        }else {
            binding.appIcon.visibility = View.INVISIBLE
            binding.fileIcon.visibility = View.INVISIBLE
            binding.loadView.visibility = View.VISIBLE
            binding.configLayout.visibility = View.INVISIBLE
            binding.rightLayout.visibility = View.INVISIBLE
        }
    }

    private fun setAlpha(view: View, star: Float, end: Float, duration: Long){
        val ofFloat = ObjectAnimator.ofFloat(view, "alpha", star, end)
        ofFloat.duration = duration
        ofFloat.start()
    }

    private fun setIcon(binding: ItemSimpleFileBinding, name: String, path: String){
        when((if (name.contains(".")) name.substring(name.lastIndexOf(".") + 1) else "default").lowercase()){
            "apk" -> {
                binding.fileIcon.setImageResource(R.drawable.file_icon_apk_phone_mirror)
            }
            "mp3", "wav" ->{
                binding.fileIcon.setImageResource(R.drawable.file_icon_music_mirror)
            }
            "mp4", "3gp" , "avi", "mkv", "flv", "vob"-> {
                if (path.startsWith("${Environment.getExternalStorageDirectory().path}/Android/data")){
                    Observable.create<ByteArray> {
                        val file = File(path)
                        it.onNext(try {
                            file.openInputStream().readBytes()
                        }catch (e: Exception){
                            ByteArray(0)
                        })
                        it.onComplete()
                    }.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            Glide.with(activity)
                                .load(it)
                                .error(R.drawable.file_icon_video_phone)
                                .placeholder(R.drawable.file_icon_video_phone)
                                .into(binding.fileIcon)
                        }
                }else {
                    Glide.with(activity).load(path).error(R.drawable.file_icon_picture_phone).placeholder(R.drawable.file_icon_picture_phone).into(binding.fileIcon)
                }
            }
            "txt" -> {
                binding.fileIcon.setImageResource(R.drawable.doc_icon_grid_txt)
            }
            "jpg", "png", "gif", "psd", "tif", "bnp", "dng", "jpeg", "heic" ->{
                if (path.startsWith("${Environment.getExternalStorageDirectory().path}/Android/data")){
                    Observable.create<ByteArray> {
                        val file = File(path)
                        it.onNext(try {
                            file.openInputStream().readBytes()
                        }catch (e: Exception){
                            val options = BitmapFactory.Options()
                            val baos = ByteArrayOutputStream()
                            BitmapFactory.decodeResource(activity.resources, R.drawable.file_icon_picture_phone, options).compress(Bitmap.CompressFormat.PNG, 100, baos)
                            baos.toByteArray()
                        })
                        it.onComplete()
                    }.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            Glide.with(activity)
                                .load(it)
                                .error(R.drawable.file_icon_picture_phone)
                                .placeholder(R.drawable.file_icon_picture_phone)
                                .into(binding.fileIcon)
                        }
                }else {
                    Glide.with(activity).load(path).error(R.drawable.file_icon_picture_phone).placeholder(R.drawable.file_icon_picture_phone).into(binding.fileIcon)
                }
            }
            "default" ->{
                binding.fileIcon.setImageResource(R.drawable.doc_icon_grid_default)
            }
            "doc" ->{
                binding.fileIcon.setImageResource(R.drawable.file_icon_word_phone_mirror)
            }
            "xls" -> {
                binding.fileIcon.setImageResource(R.drawable.doc_icon_grid_xls)
            }
            "zip", "7z", "rar", "jar", "aar", "iapp" ->{
                binding.fileIcon.setImageResource(R.drawable.file_icon_zip_phone_mirror)
            }
            else -> {
                binding.fileIcon.setImageResource(R.drawable.doc_icon_grid_default)
            }
        }
    }

    private fun openDirectory(name: String){
        val value = ArrayList<SimpleFileBean>()
        value.addAll(viewModel.filePath.value!!)
        val element = SimpleFileBean()
        element.name = name
        val layoutManager =
            Objects.requireNonNull(recyclerView.layoutManager) as StaggeredGridLayoutManager
        val mFirstVisibleItems =
            layoutManager.findFirstVisibleItemPositions(IntArray(layoutManager.spanCount))[0]
        try {
            value[value.size - 1].position = mFirstVisibleItems
        }catch (e: Exception){  }
        element.path = "${viewModel.getPath()}/$name"
        value.add(element)
        viewModel.filePath.value = value
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private var showPosition = true
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: String){
        this.list.clear()
        val map = HashMap<Int, Any>()
        map[1] = "loading..."
        this.list.add(map)
        notifyDataSetChanged()
        Observable.create {
            try {
                val listFiles = File(list).listFiles()
                RankFile.orderByType(listFiles)
                it.onNext(listFiles)
            }catch (e : Exception){
                e.printStackTrace()
                Handler(activity.mainLooper).post {
                    this.list.clear()
                    notifyDataSetChanged()
                    val field = HashMap<Int, Any>()
                    field[1] = "field"
                    this.list.add(field)
                    notifyDataSetChanged()
                }
            }
            it.onComplete()
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (viewModel.getPath() == list){
                    val files = it as Array<File?>
                    if (files.isEmpty()){
                        this.list.clear()
                        notifyDataSetChanged()
                        val none = HashMap<Int, Any>()
                        none[1] = "none"
                        this.list.add(none)
                        notifyDataSetChanged()
                    }else {
                        this.list.clear()
                        notifyDataSetChanged()
                        for (index in files.indices ){
                            if (files[index] != null) {
                                val item = HashMap<Int, Any>()
                                item[0] = files[index]!!
                                this.list.add(item)
                                notifyItemInserted(index)
                            }
                        }
                    }
                    recyclerView.post {
                        recyclerView.scrollToPosition(if (!showPosition)viewModel.filePath.value!![viewModel.filePath.value!!.size - 1].position else viewModel.listPosition.value!!)
                        showPosition = false
                    }
                }
            }
    }

    class FileHolder (var binding: ItemSimpleFileBinding
    ) : RecyclerView.ViewHolder(binding.root)
}