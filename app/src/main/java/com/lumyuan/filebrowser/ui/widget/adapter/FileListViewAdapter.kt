package com.lumyuan.filebrowser.ui.widget.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.lumyuan.filebrowser.R
import com.lumyuan.filebrowser.databinding.ItemSimpleFileBinding

class FileListViewAdapter(private val context: Context,
                          private val list: ArrayList<String>): BaseAdapter() {

    private val bindings = ArrayList<ItemSimpleFileBinding>()
    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong();
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: ItemSimpleFileBinding = ItemSimpleFileBinding.inflate(LayoutInflater.from(context))

        return binding.root
    }
}