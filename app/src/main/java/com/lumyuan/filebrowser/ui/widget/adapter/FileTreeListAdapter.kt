package com.lumyuan.filebrowser.ui.widget.adapter

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewbinding.ViewBinding
import com.lumyuan.androidfilemanager.file.File
import com.lumyuan.filebrowser.R
import com.lumyuan.filebrowser.base.BaseActivity
import com.lumyuan.filebrowser.databinding.ItemTreeBinding
import com.lumyuan.filebrowser.pojo.FileItemBean
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.schedulers.Schedulers

class FileTreeListAdapter(private val activity: BaseActivity) :
    RecyclerView.Adapter<FileTreeListAdapter.TreeHolder>() {

    private val list = ArrayList<File?>()

    class TreeHolder(val viewBinding: ItemTreeBinding):
        RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreeHolder {
        val binding = ItemTreeBinding.bind(View.inflate(activity, R.layout.item_tree, null))
        return TreeHolder(binding)
    }

    override fun onBindViewHolder(holder: TreeHolder, position: Int) {
        val binding = holder.viewBinding
        val file = list[position]
        val bean = FileItemBean()
        binding.treeLineDown.visibility = if (list.size - 1 == position) View.INVISIBLE else View.VISIBLE
        Observable.create {
            bean.name = file!!.getName()
            bean.path = file.getPath()
            bean.isDirectory = file.isDirectory()
            it.onNext("")
            it.onComplete()
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.collapseItemIcon.alpha = if (bean.isDirectory) 1F else 0F
                binding.itemName.text = bean.name
                if (bean.isDirectory){
                    binding.treeFolderIcon.setImageResource(R.mipmap.ic_folder_tree)
                    val fl = ArrayList<File?>()
                    Observable.create<String> {
                        try {
                            fl.addAll(file!!.listFiles().asList())
                        }catch (e: Exception){
                            e.printStackTrace()
                        }
                        it.onNext("")
                        it.onComplete()
                    }.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {  }

                    binding.itemInfoLayout.setOnClickListener {
                        val animator = ObjectAnimator.ofFloat(
                            binding.collapseItemIcon,
                            "rotation",
                            if (binding.treeListLayout.visibility == View.GONE) 90F else 0F
                        )
                        animator.interpolator = DecelerateInterpolator()
                        animator.duration = 400
                        animator.start()

                        if (binding.treeListLayout.visibility == View.GONE){
                            binding.treeLineDown.visibility = View.INVISIBLE
                            val layoutManager =
                                StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                            binding.itemList.layoutManager = layoutManager
                            val adapter = FileTreeListAdapter(activity)
                            if (fl.size == 0){
                                adapter.updateList(file!!)
                            }else {
                                adapter.updateList(fl)
                            }
                            binding.itemList.adapter = adapter
                            binding.treeListLayout.visibility = View.VISIBLE
                        }else {
                            binding.treeLineDown.visibility = View.VISIBLE
                            binding.treeListLayout.visibility = View.GONE
                        }
                    }
                }else {
                    binding.treeFolderIcon.setImageResource(R.drawable.doc_icon_grid_default)
                }
            }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(file: File){
        list.clear()
        Observable.create {
            try {
                it.onNext(file.listFiles().asList())
            }catch (e: Exception){
                it.onNext(ArrayList())
            }
            it.onComplete()
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                list.addAll(it)
                notifyDataSetChanged()
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateList(list: List<File?>){
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }
}