package com.lumyuan.filebrowser.ui.widget.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.lumyuan.androidfilemanager.AndroidFileManagerApplication
import com.lumyuan.androidfilemanager.dao.FIleOrderRules
import com.lumyuan.filebrowser.R
import com.lumyuan.filebrowser.databinding.DialogFileSortBinding
import com.lumyuan.filebrowser.model.FileChangedViewModel
import com.lxj.xpopup.core.AttachPopupView

class FileSortDialog(context: Context) : AttachPopupView(context) {
    override fun getImplLayoutId(): Int {
        return R.layout.dialog_file_sort
    }

    lateinit var binding: DialogFileSortBinding
    lateinit var viewModel: FileChangedViewModel
    override fun onCreate() {
        super.onCreate()
        binding = DialogFileSortBinding.bind(rootView.findViewById(R.id.root))
        viewModel = ViewModelProvider(context as AppCompatActivity).get(FileChangedViewModel::class.java)
        checkRules(AndroidFileManagerApplication.getOrderRules())
        binding.fileNameLayout.setOnClickListener {
            when(AndroidFileManagerApplication.getOrderRules()){
                FIleOrderRules.ORDER_BY_NAME + FIleOrderRules.ORDER_BY_SIMPLE ->{
                    val rules = FIleOrderRules.ORDER_BY_NAME + FIleOrderRules.ORDER_BY_INVERSE
                    checkRules(rules)
                    AndroidFileManagerApplication.saveOrderRules(rules)
                    viewModel.orderRules.value = rules
                }
                FIleOrderRules.ORDER_BY_NAME + FIleOrderRules.ORDER_BY_INVERSE ->{
                    val rules = FIleOrderRules.ORDER_BY_NAME + FIleOrderRules.ORDER_BY_SIMPLE
                    checkRules(rules)
                    AndroidFileManagerApplication.saveOrderRules(rules)
                    viewModel.orderRules.value = rules
                }
                else ->{
                    val rules = FIleOrderRules.ORDER_BY_NAME + FIleOrderRules.ORDER_BY_SIMPLE
                    checkRules(rules)
                    AndroidFileManagerApplication.saveOrderRules(rules)
                    viewModel.orderRules.value = rules
                }
            }
        }

        binding.fileSizeLayout.setOnClickListener {
            when(AndroidFileManagerApplication.getOrderRules()){
                FIleOrderRules.ORDER_BY_SIZE + FIleOrderRules.ORDER_BY_SIMPLE ->{
                    val rules = FIleOrderRules.ORDER_BY_SIZE + FIleOrderRules.ORDER_BY_INVERSE
                    checkRules(rules)
                    AndroidFileManagerApplication.saveOrderRules(rules)
                    viewModel.orderRules.value = rules
                }
                FIleOrderRules.ORDER_BY_SIZE + FIleOrderRules.ORDER_BY_INVERSE ->{
                    val rules = FIleOrderRules.ORDER_BY_SIZE + FIleOrderRules.ORDER_BY_SIMPLE
                    checkRules(rules)
                    AndroidFileManagerApplication.saveOrderRules(rules)
                    viewModel.orderRules.value = rules
                }
                else ->{
                    val rules = FIleOrderRules.ORDER_BY_SIZE + FIleOrderRules.ORDER_BY_SIMPLE
                    checkRules(rules)
                    AndroidFileManagerApplication.saveOrderRules(rules)
                    viewModel.orderRules.value = rules
                }
            }
        }

        binding.updateLayout.setOnClickListener {
            when(AndroidFileManagerApplication.getOrderRules()){
                FIleOrderRules.ORDER_BY_DATE + FIleOrderRules.ORDER_BY_SIMPLE ->{
                    val rules = FIleOrderRules.ORDER_BY_DATE + FIleOrderRules.ORDER_BY_INVERSE
                    checkRules(rules)
                    AndroidFileManagerApplication.saveOrderRules(rules)
                    viewModel.orderRules.value = rules
                }
                FIleOrderRules.ORDER_BY_DATE + FIleOrderRules.ORDER_BY_INVERSE ->{
                    val rules = FIleOrderRules.ORDER_BY_DATE + FIleOrderRules.ORDER_BY_SIMPLE
                    checkRules(rules)
                    AndroidFileManagerApplication.saveOrderRules(rules)
                    viewModel.orderRules.value = rules
                }
                else ->{
                    val rules = FIleOrderRules.ORDER_BY_DATE + FIleOrderRules.ORDER_BY_SIMPLE
                    checkRules(rules)
                    AndroidFileManagerApplication.saveOrderRules(rules)
                    viewModel.orderRules.value = rules
                }
            }
        }

        binding.fileTypeLayout.setOnClickListener {
            val orderRules = AndroidFileManagerApplication.getOrderRules()
            println(orderRules)
            when(orderRules){
                FIleOrderRules.ORDER_BY_TYPE + FIleOrderRules.ORDER_BY_SIMPLE ->{
                    val rules = FIleOrderRules.ORDER_BY_TYPE + FIleOrderRules.ORDER_BY_INVERSE
                    checkRules(rules)
                    AndroidFileManagerApplication.saveOrderRules(rules)
                    viewModel.orderRules.value = rules
                }
                FIleOrderRules.ORDER_BY_TYPE + FIleOrderRules.ORDER_BY_INVERSE ->{
                    val rules = FIleOrderRules.ORDER_BY_TYPE + FIleOrderRules.ORDER_BY_SIMPLE
                    checkRules(rules)
                    AndroidFileManagerApplication.saveOrderRules(rules)
                    viewModel.orderRules.value = rules
                }
                else ->{
                    val rules = FIleOrderRules.ORDER_BY_TYPE + FIleOrderRules.ORDER_BY_SIMPLE
                    checkRules(rules)
                    AndroidFileManagerApplication.saveOrderRules(rules)
                    viewModel.orderRules.value = rules
                }
            }
        }
    }

    @SuppressLint("ResourceType")
    private fun checkRules(rules: Int){
        when(rules){
            FIleOrderRules.ORDER_BY_NAME + FIleOrderRules.ORDER_BY_SIMPLE->{
                binding.fileNameLayout.setBackgroundColor(Color.parseColor(context.getString(R.color.sort_color_bg)))
                binding.fileNameText.setTextColor(Color.parseColor(context.getString(R.color.sort_color)))
                binding.fileNameUp.setImageResource(R.drawable.fab_up_selects)
                binding.fileNameDown.setImageResource(R.drawable.fab_down_normals)

                binding.fileSizeLayout.setBackgroundColor(Color.parseColor(context.getString(R.color.white)))
                binding.fileSizeText.setTextColor(Color.parseColor(context.getString(R.color.gray_text)))
                binding.fileSizeUp.setImageResource(R.drawable.fab_up_normals)
                binding.fileSizeDown.setImageResource(R.drawable.fab_down_normals)

                binding.updateLayout.setBackgroundColor(Color.parseColor(context.getString(R.color.white)))
                binding.updateText.setTextColor(Color.parseColor(context.getString(R.color.gray_text)))
                binding.updateUp.setImageResource(R.drawable.fab_up_normals)
                binding.updateDown.setImageResource(R.drawable.fab_down_normals)

                binding.fileTypeLayout.setBackgroundColor(Color.parseColor(context.getString(R.color.white)))
                binding.fileTypeText.setTextColor(Color.parseColor(context.getString(R.color.gray_text)))
                binding.fileTypeUp.setImageResource(R.drawable.fab_up_normals)
                binding.fileTypeDown.setImageResource(R.drawable.fab_down_normals)
            }
            FIleOrderRules.ORDER_BY_SIZE + FIleOrderRules.ORDER_BY_SIMPLE->{
                binding.fileSizeLayout.setBackgroundColor(Color.parseColor(context.getString(R.color.sort_color_bg)))
                binding.fileSizeText.setTextColor(Color.parseColor(context.getString(R.color.sort_color)))
                binding.fileSizeUp.setImageResource(R.drawable.fab_up_selects)
                binding.fileSizeDown.setImageResource(R.drawable.fab_down_normals)

                binding.fileNameLayout.setBackgroundColor(Color.parseColor(context.getString(R.color.white)))
                binding.fileNameText.setTextColor(Color.parseColor(context.getString(R.color.gray_text)))
                binding.fileNameUp.setImageResource(R.drawable.fab_up_normals)
                binding.fileNameDown.setImageResource(R.drawable.fab_down_normals)

                binding.updateLayout.setBackgroundColor(Color.parseColor(context.getString(R.color.white)))
                binding.updateText.setTextColor(Color.parseColor(context.getString(R.color.gray_text)))
                binding.updateUp.setImageResource(R.drawable.fab_up_normals)
                binding.updateDown.setImageResource(R.drawable.fab_down_normals)

                binding.fileTypeLayout.setBackgroundColor(Color.parseColor(context.getString(R.color.white)))
                binding.fileTypeText.setTextColor(Color.parseColor(context.getString(R.color.gray_text)))
                binding.fileTypeUp.setImageResource(R.drawable.fab_up_normals)
                binding.fileTypeDown.setImageResource(R.drawable.fab_down_normals)
            }
            FIleOrderRules.ORDER_BY_DATE + FIleOrderRules.ORDER_BY_SIMPLE->{
                binding.updateLayout.setBackgroundColor(Color.parseColor(context.getString(R.color.sort_color_bg)))
                binding.updateText.setTextColor(Color.parseColor(context.getString(R.color.sort_color)))
                binding.updateUp.setImageResource(R.drawable.fab_up_selects)
                binding.updateDown.setImageResource(R.drawable.fab_down_normals)

                binding.fileSizeLayout.setBackgroundColor(Color.parseColor(context.getString(R.color.white)))
                binding.fileSizeText.setTextColor(Color.parseColor(context.getString(R.color.gray_text)))
                binding.fileSizeUp.setImageResource(R.drawable.fab_up_normals)
                binding.fileSizeDown.setImageResource(R.drawable.fab_down_normals)

                binding.fileNameLayout.setBackgroundColor(Color.parseColor(context.getString(R.color.white)))
                binding.fileNameText.setTextColor(Color.parseColor(context.getString(R.color.gray_text)))
                binding.fileNameUp.setImageResource(R.drawable.fab_up_normals)
                binding.fileNameDown.setImageResource(R.drawable.fab_down_normals)

                binding.fileTypeLayout.setBackgroundColor(Color.parseColor(context.getString(R.color.white)))
                binding.fileTypeText.setTextColor(Color.parseColor(context.getString(R.color.gray_text)))
                binding.fileTypeUp.setImageResource(R.drawable.fab_up_normals)
                binding.fileTypeDown.setImageResource(R.drawable.fab_down_normals)
            }
            FIleOrderRules.ORDER_BY_TYPE + FIleOrderRules.ORDER_BY_SIMPLE->{
                binding.fileTypeLayout.setBackgroundColor(Color.parseColor(context.getString(R.color.sort_color_bg)))
                binding.fileTypeText.setTextColor(Color.parseColor(context.getString(R.color.sort_color)))
                binding.fileTypeUp.setImageResource(R.drawable.fab_up_selects)
                binding.fileTypeDown.setImageResource(R.drawable.fab_down_normals)

                binding.fileSizeLayout.setBackgroundColor(Color.parseColor(context.getString(R.color.white)))
                binding.fileSizeText.setTextColor(Color.parseColor(context.getString(R.color.gray_text)))
                binding.fileSizeUp.setImageResource(R.drawable.fab_up_normals)
                binding.fileSizeDown.setImageResource(R.drawable.fab_down_normals)

                binding.updateLayout.setBackgroundColor(Color.parseColor(context.getString(R.color.white)))
                binding.updateText.setTextColor(Color.parseColor(context.getString(R.color.gray_text)))
                binding.updateUp.setImageResource(R.drawable.fab_up_normals)
                binding.updateDown.setImageResource(R.drawable.fab_down_normals)

                binding.fileNameLayout.setBackgroundColor(Color.parseColor(context.getString(R.color.white)))
                binding.fileNameText.setTextColor(Color.parseColor(context.getString(R.color.gray_text)))
                binding.fileNameUp.setImageResource(R.drawable.fab_up_normals)
                binding.fileNameDown.setImageResource(R.drawable.fab_down_normals)
            }

            //TODO
            FIleOrderRules.ORDER_BY_NAME + FIleOrderRules.ORDER_BY_INVERSE->{
                binding.fileNameLayout.setBackgroundColor(Color.parseColor(context.getString(R.color.sort_color_bg)))
                binding.fileNameText.setTextColor(Color.parseColor(context.getString(R.color.sort_color)))
                binding.fileNameUp.setImageResource(R.drawable.fab_up_normals)
                binding.fileNameDown.setImageResource(R.drawable.fab_down_selects)

                binding.fileSizeLayout.setBackgroundColor(Color.parseColor(context.getString(R.color.white)))
                binding.fileSizeText.setTextColor(Color.parseColor(context.getString(R.color.gray_text)))
                binding.fileSizeUp.setImageResource(R.drawable.fab_up_normals)
                binding.fileSizeDown.setImageResource(R.drawable.fab_down_normals)

                binding.updateLayout.setBackgroundColor(Color.parseColor(context.getString(R.color.white)))
                binding.updateText.setTextColor(Color.parseColor(context.getString(R.color.gray_text)))
                binding.updateUp.setImageResource(R.drawable.fab_up_normals)
                binding.updateDown.setImageResource(R.drawable.fab_down_normals)

                binding.fileTypeLayout.setBackgroundColor(Color.parseColor(context.getString(R.color.white)))
                binding.fileTypeText.setTextColor(Color.parseColor(context.getString(R.color.gray_text)))
                binding.fileTypeUp.setImageResource(R.drawable.fab_up_normals)
                binding.fileTypeDown.setImageResource(R.drawable.fab_down_normals)
            }
            FIleOrderRules.ORDER_BY_SIZE + FIleOrderRules.ORDER_BY_INVERSE->{
                binding.fileSizeLayout.setBackgroundColor(Color.parseColor(context.getString(R.color.sort_color_bg)))
                binding.fileSizeText.setTextColor(Color.parseColor(context.getString(R.color.sort_color)))
                binding.fileSizeUp.setImageResource(R.drawable.fab_up_normals)
                binding.fileSizeDown.setImageResource(R.drawable.fab_down_selects)

                binding.fileNameLayout.setBackgroundColor(Color.parseColor(context.getString(R.color.white)))
                binding.fileNameText.setTextColor(Color.parseColor(context.getString(R.color.gray_text)))
                binding.fileNameUp.setImageResource(R.drawable.fab_up_normals)
                binding.fileNameDown.setImageResource(R.drawable.fab_down_normals)

                binding.updateLayout.setBackgroundColor(Color.parseColor(context.getString(R.color.white)))
                binding.updateText.setTextColor(Color.parseColor(context.getString(R.color.gray_text)))
                binding.updateUp.setImageResource(R.drawable.fab_up_normals)
                binding.updateDown.setImageResource(R.drawable.fab_down_normals)

                binding.fileTypeLayout.setBackgroundColor(Color.parseColor(context.getString(R.color.white)))
                binding.fileTypeText.setTextColor(Color.parseColor(context.getString(R.color.gray_text)))
                binding.fileTypeUp.setImageResource(R.drawable.fab_up_normals)
                binding.fileTypeDown.setImageResource(R.drawable.fab_down_normals)
            }
            FIleOrderRules.ORDER_BY_DATE + FIleOrderRules.ORDER_BY_INVERSE->{
                binding.updateLayout.setBackgroundColor(Color.parseColor(context.getString(R.color.sort_color_bg)))
                binding.updateText.setTextColor(Color.parseColor(context.getString(R.color.sort_color)))
                binding.updateUp.setImageResource(R.drawable.fab_up_normals)
                binding.updateDown.setImageResource(R.drawable.fab_down_selects)

                binding.fileSizeLayout.setBackgroundColor(Color.parseColor(context.getString(R.color.white)))
                binding.fileSizeText.setTextColor(Color.parseColor(context.getString(R.color.gray_text)))
                binding.fileSizeUp.setImageResource(R.drawable.fab_up_normals)
                binding.fileSizeDown.setImageResource(R.drawable.fab_down_normals)

                binding.fileNameLayout.setBackgroundColor(Color.parseColor(context.getString(R.color.white)))
                binding.fileNameText.setTextColor(Color.parseColor(context.getString(R.color.gray_text)))
                binding.fileNameUp.setImageResource(R.drawable.fab_up_normals)
                binding.fileNameDown.setImageResource(R.drawable.fab_down_normals)

                binding.fileTypeLayout.setBackgroundColor(Color.parseColor(context.getString(R.color.white)))
                binding.fileTypeText.setTextColor(Color.parseColor(context.getString(R.color.gray_text)))
                binding.fileTypeUp.setImageResource(R.drawable.fab_up_normals)
                binding.fileTypeDown.setImageResource(R.drawable.fab_down_normals)
            }
            FIleOrderRules.ORDER_BY_TYPE + FIleOrderRules.ORDER_BY_INVERSE->{
                binding.fileTypeLayout.setBackgroundColor(Color.parseColor(context.getString(R.color.sort_color_bg)))
                binding.fileTypeText.setTextColor(Color.parseColor(context.getString(R.color.sort_color)))
                binding.fileTypeUp.setImageResource(R.drawable.fab_up_normals)
                binding.fileTypeDown.setImageResource(R.drawable.fab_down_selects)

                binding.fileSizeLayout.setBackgroundColor(Color.parseColor(context.getString(R.color.white)))
                binding.fileSizeText.setTextColor(Color.parseColor(context.getString(R.color.gray_text)))
                binding.fileSizeUp.setImageResource(R.drawable.fab_up_normals)
                binding.fileSizeDown.setImageResource(R.drawable.fab_down_normals)

                binding.updateLayout.setBackgroundColor(Color.parseColor(context.getString(R.color.white)))
                binding.updateText.setTextColor(Color.parseColor(context.getString(R.color.gray_text)))
                binding.updateUp.setImageResource(R.drawable.fab_up_normals)
                binding.updateDown.setImageResource(R.drawable.fab_down_normals)

                binding.fileNameLayout.setBackgroundColor(Color.parseColor(context.getString(R.color.white)))
                binding.fileNameText.setTextColor(Color.parseColor(context.getString(R.color.gray_text)))
                binding.fileNameUp.setImageResource(R.drawable.fab_up_normals)
                binding.fileNameDown.setImageResource(R.drawable.fab_down_normals)
            }
        }
    }
}
