package com.lumyuan.filebrowser.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.lumyuan.androidfilemanager.AndroidFileManagerApplication
import com.lumyuan.androidfilemanager.file.File
import com.lumyuan.androidfilemanager.utils.ExternalStoragePermissionsUtils
import com.lumyuan.filebrowser.R
import com.lumyuan.filebrowser.base.BaseActivity
import com.lumyuan.filebrowser.databinding.ActivityMainBinding
import com.lumyuan.filebrowser.model.FileChangedViewModel
import com.lumyuan.filebrowser.model.ListRootLayoutViewModel
import com.lumyuan.filebrowser.pojo.SimpleFileBean
import com.lumyuan.filebrowser.ui.widget.adapter.FileTreeListAdapter
import com.lumyuan.filebrowser.ui.widget.adapter.LabelListAdapter
import com.lumyuan.filebrowser.ui.widget.adapter.SimpleFileListAdapter
import com.lumyuan.filebrowser.ui.widget.dialog.FileSortDialog
import com.lumyuan.filebrowser.utils.AndroidInfo
import com.lumyuan.filebrowser.utils.DensityUtil
import com.lxj.xpopup.XPopup
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlin.math.abs

class MainActivity : BaseActivity(){

    private val binding by binding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ExternalStoragePermissionsUtils.getExternalStoragePermission(this)
        ExternalStoragePermissionsUtils.getAllFilesAccessPermission(this)
        ExternalStoragePermissionsUtils.getApplicationExclusiveDirectoryPermission(this)
        initLabelView()
        initSimpleFileView()
        initTreeList()
        initFilePath()

        binding.listSortButton.setOnClickListener {
            XPopup.Builder(this)
                .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                .atView(it)
                .isViewMode(true)
                .hasShadowBg(false) // 去掉半透明背景
                .asCustom(FileSortDialog(this))
                .show()
        }
    }

    private lateinit var viewModel: FileChangedViewModel
    private lateinit var layoutViewModel: ListRootLayoutViewModel
    private lateinit var labelListAdapter: LabelListAdapter
    private fun initLabelView() {
        viewModel = ViewModelProvider(this)[FileChangedViewModel::class.java]
        layoutViewModel = ViewModelProvider(this)[ListRootLayoutViewModel::class.java]
        labelListAdapter = LabelListAdapter(this, binding.labelList)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        layoutManager.stackFromEnd = true
        binding.labelList.layoutManager = layoutManager
        binding.labelList.adapter = labelListAdapter
    }

    private lateinit var simpleFileListAdapter: SimpleFileListAdapter
    private fun initSimpleFileView() {
        simpleFileListAdapter = SimpleFileListAdapter(this, binding.simpleList)
        val layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        binding.simpleList.layoutManager = layoutManager
        binding.simpleList.adapter = simpleFileListAdapter

        binding.simpleList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val lm = recyclerView.layoutManager as StaggeredGridLayoutManager
                    val mFirstVisibleItems =
                        lm.findFirstVisibleItemPositions(IntArray(layoutManager.spanCount))
                    viewModel.action.value = SimpleFileListAdapter.ACTION_ORDER
                    viewModel.listPosition.value = mFirstVisibleItems[0]
                }
            }
        })

        viewModel.orderRules.observe(this) {
            Observable.create { path ->
                path.onNext(getPath())
                path.onComplete()
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { path ->
                    simpleFileListAdapter.updateList(path, it, SimpleFileListAdapter.ACTION_ORDER)
                }
        }
    }

    private fun initTreeList() {
        binding.treeList.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        val adapter = FileTreeListAdapter(this)
        adapter.updateList(File("/"))
        binding.treeList.adapter = adapter
    }

    private fun initFilePath() {
        java.io.File("").listFiles()
        //更新路径以及UI视图
        viewModel.filePath.observe(this) {
            if (it != null) {
                if (it.size - labelListAdapter.list.size == 1) {
                    labelListAdapter.addList(it[it.size - 1])
                } else if (labelListAdapter.list.size - it.size == 1) {
                    labelListAdapter.deleteList(it.size)
                } else if (labelListAdapter.list.size - it.size > 1) {
                    labelListAdapter.jumpList(it.size)
                } else {
                    labelListAdapter.updateList(it)
                }
                Observable.create { fileList ->
                    fileList.onNext(viewModel.getPath())
                    fileList.onComplete()
                }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { fileList ->
                        simpleFileListAdapter.updateList(
                            fileList,
                            viewModel.orderRules.value!!,
                            viewModel.action.value!!
                        )
                    }
            }
        }


        if (viewModel.filePath.value == null) {
            Thread {
                val fileList = ArrayList<SimpleFileBean>()
                for (i in getPath().split("/")) {
                    fileList.add(SimpleFileBean().apply {
                        this.name = i
                    })
                }
                runOnUiThread {
                    viewModel.filePath.value = fileList
                }
            }.start()
        }
    }

    private var first = false
    override fun onResume() {
        super.onResume()
        if (!first) {
            first = true

            initListRootListener()
        }
    }

    private var downX = 0F
    private var downY = 0F
    private var len = 0F
    private var treeMaxWidth = 0F
    private var changeLength = 0F
    private var canTouch = false
    private var dismiss = false

    @SuppressLint("ClickableViewAccessibility", "ResourceType")
    private fun initListRootListener() {

        val info = AndroidInfo(this)
        val switchWidth = DensityUtil.dip2px(this, 30F)
        treeMaxWidth = info.width() / 2F

        binding.treeRoot.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (right == switchWidth){
                val ofFloat = ObjectAnimator.ofFloat(binding.switchIcon, "rotation", 0F)
                ofFloat.duration = 400
                ofFloat.interpolator = DecelerateInterpolator()
                ofFloat.start()
            }else {
                val ofFloat = ObjectAnimator.ofFloat(binding.switchIcon, "rotation", 180F)
                ofFloat.duration = 400
                ofFloat.interpolator = DecelerateInterpolator()
                ofFloat.start()
            }
        }

        try {
            val layoutParams = LinearLayout.LayoutParams(
                if (layoutViewModel.viewLength.value!! > 0.4) {
                    (treeMaxWidth * layoutViewModel.viewLength.value!!).toInt()
                } else {
                    switchWidth
                },
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            if (layoutParams.width > switchWidth)
                binding.treeHorizontalView.visibility = View.VISIBLE
            binding.treeRoot.layoutParams = layoutParams
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding.treeSwitch.setOnTouchListener { v, event ->
            val action = event.action
            val rawX = event.rawX
            val rawY = event.rawY
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    downX = rawX
                    downY = rawY
                    dismiss = true
                    Handler(mainLooper).postDelayed({
                        if (dismiss) {
                            canTouch = true
                            val flag =
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) HapticFeedbackConstants.GESTURE_END else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) HapticFeedbackConstants.KEYBOARD_RELEASE else HapticFeedbackConstants.VIRTUAL_KEY
                            v.performHapticFeedback(flag)
                            binding.treeSwitch.setBackgroundResource(R.drawable.shape_tree_switch_sel)
                        }
                    }, 200)
                }
                MotionEvent.ACTION_MOVE -> {
                    if (canTouch) {
                        changeLength = rawX - downX
                        len = binding.treeRoot.width + changeLength
                        if (len < switchWidth) {
                            len = switchWidth.toFloat()
                        }
                        if (len > treeMaxWidth) {
                            len = treeMaxWidth
                        }
                        if (binding.treeHorizontalView.visibility != View.VISIBLE)
                            binding.treeHorizontalView.visibility = View.VISIBLE
                        layoutViewModel.viewLength.value = len / treeMaxWidth
                        val layoutParams = LinearLayout.LayoutParams(
                            len.toInt(),
                            LinearLayout.LayoutParams.MATCH_PARENT
                        )
                        binding.treeRoot.layoutParams = layoutParams
                        downX = rawX
                    } else if (!canTouch && (event.rawX - downX > 50 || event.rawY - downY > 50)) {
                        dismiss = false
                        canTouch = false
                        binding.treeSwitch.setBackgroundResource(R.drawable.shape_tree_switch_def)
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (!canTouch){
                        dismiss = false
                        canTouch = false
                        binding.treeSwitch.setBackgroundResource(R.drawable.shape_tree_switch_def)
                        val ofInt = ValueAnimator.ofInt(binding.treeRoot.width, if (layoutViewModel.viewLength.value!! > 0.3F) switchWidth else treeMaxWidth.toInt())
                        ofInt.duration = 400
                        ofInt.interpolator = DecelerateInterpolator()
                        ofInt.addUpdateListener {
                            if (binding.treeHorizontalView.visibility != View.VISIBLE)
                                binding.treeHorizontalView.visibility = View.VISIBLE
                            val animatedValue = it.animatedValue as Int
                            val layoutParams = LinearLayout.LayoutParams(
                                animatedValue,
                                LinearLayout.LayoutParams.MATCH_PARENT
                            )
                            binding.treeRoot.layoutParams = layoutParams
                            layoutViewModel.viewLength.value = animatedValue / treeMaxWidth
                        }
                        ofInt.addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                super.onAnimationEnd(animation)
                            }
                        })
                        ofInt.start()
                        return@setOnTouchListener true
                    }
                    dismiss = false
                    canTouch = false
                    binding.treeSwitch.setBackgroundResource(R.drawable.shape_tree_switch_def)
                    val value = layoutViewModel.viewLength.value!!
                    if (value < 0.3) {
                        val ofInt = ValueAnimator.ofInt(binding.treeRoot.width, switchWidth)
                        ofInt.duration = 400
                        ofInt.interpolator = DecelerateInterpolator()
                        ofInt.addUpdateListener {
                            val animatedValue = it.animatedValue as Int
                            val layoutParams = LinearLayout.LayoutParams(
                                animatedValue,
                                LinearLayout.LayoutParams.MATCH_PARENT
                            )
                            binding.treeRoot.layoutParams = layoutParams
                            layoutViewModel.viewLength.value = animatedValue / treeMaxWidth
                        }
                        ofInt.start()
                    }else if (value < 0.5){
                        val ofInt = ValueAnimator.ofInt(binding.treeRoot.width, (treeMaxWidth * 0.5).toInt())
                        ofInt.duration = 400
                        ofInt.interpolator = DecelerateInterpolator()
                        ofInt.addUpdateListener {
                            val animatedValue = it.animatedValue as Int
                            val layoutParams = LinearLayout.LayoutParams(
                                animatedValue,
                                LinearLayout.LayoutParams.MATCH_PARENT
                            )
                            binding.treeRoot.layoutParams = layoutParams
                            layoutViewModel.viewLength.value = animatedValue / treeMaxWidth
                        }
                        ofInt.start()
                    }
                }
            }
            true
        }
    }

    private var isRun = false
    private fun showTreeSwitch(isShow: Boolean) {
        isRun = true
        binding.treeSwitch.postDelayed({
            if (!canTouch) {
                val ofFloat = ObjectAnimator.ofFloat(
                    binding.treeRoot,
                    "translationX",
                    binding.treeRoot.translationX,
                    if (isShow) 0F else -DensityUtil.dip2px(this@MainActivity, 30F).toFloat()
                )
                ofFloat.duration = 400
                ofFloat.interpolator = DecelerateInterpolator()
                ofFloat.start()
            }
            isRun = false
        }, if (isShow) 0 else 4000)
    }

    private fun getPath(): String {
        //初始化路径
        return if (viewModel.filePath.value == null)
            Environment.getExternalStorageDirectory().absolutePath
        else
            viewModel.getPath()
    }

    private var time = 0L
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                return if ((!AndroidFileManagerApplication.hasRoot && viewModel.filePath.value!!.size > 4)
                    || (viewModel.filePath.value!!.size > 1 && AndroidFileManagerApplication.hasRoot)
                ) {
                    val newFiles = ArrayList<SimpleFileBean>()
                    for (i in 0 until viewModel.filePath.value!!.size - 1) {
                        newFiles.add(viewModel.filePath.value!![i])
                    }
                    viewModel.action.value = SimpleFileListAdapter.ACTION_BACK
                    viewModel.filePath.value = newFiles
                    true
                } else {
                    if (System.currentTimeMillis() - time > 2000) {
                        Toast.makeText(
                            this,
                            "再按一次退出${getString(R.string.app_name)}",
                            Toast.LENGTH_SHORT
                        ).show()
                        time = System.currentTimeMillis()
                    } else {
                        finish()
                    }
                    true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

}