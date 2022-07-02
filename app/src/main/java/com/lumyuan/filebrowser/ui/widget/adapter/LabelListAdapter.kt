package com.lumyuan.filebrowser.ui.widget.adapter

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lumyuan.filebrowser.R
import com.lumyuan.filebrowser.databinding.ItemLabelBinding
import com.lumyuan.filebrowser.model.FileChangedViewModel
import com.lumyuan.filebrowser.pojo.SimpleFileBean
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

class LabelListAdapter(private val activity: AppCompatActivity, private val recyclerView: RecyclerView)
    : RecyclerView.Adapter<LabelListAdapter.LabelHolder>() {

    private val list: ArrayList<SimpleFileBean> = ArrayList()

    private var viewModel: FileChangedViewModel = ViewModelProvider(activity).get(FileChangedViewModel::class.java)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelHolder {
        val binding = ItemLabelBinding.bind(View.inflate(parent.context, R.layout.item_label, null))
        return LabelHolder(binding)
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: LabelHolder, position: Int) {
        val binding = holder.binding
        val path = getPath(position)

        val name = list[position].name
        binding.labelName.text = if (name == "") "根目录" else name

        binding.root.setOnClickListener {
            Observable.create {
                val files = ArrayList<SimpleFileBean>()
                for (i in 0..position){
                    files.add(this.list[i])
                }
                it.onNext(files)
                it.onComplete()
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    viewModel.filePath.value = it
                }
        }

        binding.root.setOnLongClickListener {
            val cm = activity.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val mClipData = ClipData.newPlainText("Label", getPath(position))
            cm.setPrimaryClip(mClipData)
            Toast.makeText(activity, "已将路径复制到剪切板中", Toast.LENGTH_SHORT).show()
            true
        }

        binding.marginStart.visibility = if (position == 0)
            View.VISIBLE
        else
            View.GONE

        binding.marginEnd.visibility = if (position == itemCount - 1)
            View.VISIBLE
        else
            View.GONE

        if (position < itemCount - 1){
            binding.labelName.setTextColor(Color.parseColor(binding.root.context.getString(R.color.gray)))
            binding.labelName.setBackgroundResource(R.drawable.shape_label_mid)
            binding.rightIcon.visibility = View.VISIBLE
        }else {
            binding.labelName.setTextColor(Color.parseColor(binding.root.context.getString(R.color.orange)))
            binding.labelName.setBackgroundResource(R.drawable.shape_label_last)
            binding.rightIcon.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun getPath(position: Int): String{
        val stringBuilder = StringBuilder()
        for (index in 0..position){
            val name = list[index].name
            if (name != "")
                stringBuilder.append("/$name")
        }
        return if (stringBuilder.toString() == "") "/" else stringBuilder.toString()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(files: ArrayList<SimpleFileBean>){
        this.list.clear()
        this.list.addAll(files)
        notifyDataSetChanged()
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager
    }

    class LabelHolder(var binding: ItemLabelBinding
    ) : RecyclerView.ViewHolder(binding.root)
}