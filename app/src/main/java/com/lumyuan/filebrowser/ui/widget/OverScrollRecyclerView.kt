package com.lumyuan.filebrowser.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.lumyuan.filebrowser.ui.model.OverScrollDelegate

class OverScrollRecyclerView : RecyclerView, OverScrollDelegate.OverScrollable {
    private lateinit var mOverScrollDelegate: OverScrollDelegate

    constructor(context: Context) : super(context) {
        createOverScrollDelegate()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        createOverScrollDelegate()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        createOverScrollDelegate()
    }

    private fun createOverScrollDelegate() {
        mOverScrollDelegate = OverScrollDelegate(this)
        this.overScrollMode = 0
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return if (mOverScrollDelegate.onInterceptTouchEvent(ev)) true else super.onInterceptTouchEvent(
            ev
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (mOverScrollDelegate.onTouchEvent(event)) true else super.onTouchEvent(event)
    }

    @SuppressLint("MissingSuperCall")
    override fun draw(canvas: Canvas) {
        mOverScrollDelegate.draw(canvas)
    }

    fun absorbGlows(velocityX: Int, velocityY: Int) {
        mOverScrollDelegate.recyclerViewAbsorbGlows(velocityX, velocityY)
    }

    override fun superComputeVerticalScrollExtent(): Int {
        return super.computeVerticalScrollExtent()
    }

    override fun superComputeVerticalScrollOffset(): Int {
        return super.computeVerticalScrollOffset()
    }

    override fun superComputeVerticalScrollRange(): Int {
        return super.computeVerticalScrollRange()
    }

    override fun superOnTouchEvent(var1: MotionEvent?) {
        super.onTouchEvent(var1)
    }

    override fun superDraw(var1: Canvas?) {
        super.draw(var1)
    }

    override fun superAwakenScrollBars(): Boolean {
        return super.awakenScrollBars()
    }

    override fun superOverScrollBy(
        var1: Int,
        var2: Int,
        var3: Int,
        var4: Int,
        var5: Int,
        var6: Int,
        var7: Int,
        var8: Int,
        var9: Boolean
    ): Boolean {
        return super.overScrollBy(
            var1,
            var2,
            var3,
            var4,
            var5,
            var6,
            var7,
            var8,
            var9
        )
    }

    override val overScrollableView: View
        get() = this
    override val overScrollDelegate: OverScrollDelegate?
        get() = mOverScrollDelegate
}
