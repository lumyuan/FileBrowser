package com.lumyuan.filebrowser.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.ScrollView
import com.lumyuan.filebrowser.ui.model.OverScrollDelegate
import com.lumyuan.filebrowser.ui.model.OverScrollDelegate.OverScrollable

class OverScrollView : ScrollView, OverScrollable {
    override var overScrollDelegate: OverScrollDelegate? = null
        private set

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        createOverScrollDelegate(context)
    }

    private fun createOverScrollDelegate(context: Context) {
        overScrollDelegate = OverScrollDelegate(this)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return if (overScrollDelegate!!.onInterceptTouchEvent(ev)) true else super.onInterceptTouchEvent(
            ev
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (overScrollDelegate!!.onTouchEvent(event)) true else super.onTouchEvent(event)
    }

    @SuppressLint("MissingSuperCall")
    override fun draw(canvas: Canvas) {
        overScrollDelegate!!.draw(canvas)
    }

    override fun overScrollBy(
        deltaX: Int,
        deltaY: Int,
        scrollX: Int,
        scrollY: Int,
        scrollRangeX: Int,
        scrollRangeY: Int,
        maxOverScrollX: Int,
        maxOverScrollY: Int,
        isTouchEvent: Boolean
    ): Boolean {
        return overScrollDelegate!!.overScrollBy(
            deltaX,
            deltaY,
            scrollX,
            scrollY,
            scrollRangeX,
            scrollRangeY,
            maxOverScrollX,
            maxOverScrollY,
            isTouchEvent
        )
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
}
