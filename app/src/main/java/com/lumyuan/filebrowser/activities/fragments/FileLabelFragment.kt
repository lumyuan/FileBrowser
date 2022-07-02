package com.lumyuan.filebrowser.activities.fragments

import android.view.View
import com.lumyuan.filebrowser.R
import com.lumyuan.filebrowser.base.BaseFragment
import com.lumyuan.filebrowser.databinding.FileLabelFragmentBinding

class FileLabelFragment : BaseFragment(R.layout.file_label_fragment) {

    private lateinit var binding : FileLabelFragmentBinding
    override fun initView(root: View) {
        super.initView(root)
        binding = FileLabelFragmentBinding.bind(root)
    }

    override fun loadSingleData() {
        super.loadSingleData()
    }

    override fun loadData() {
        super.loadData()

    }

}