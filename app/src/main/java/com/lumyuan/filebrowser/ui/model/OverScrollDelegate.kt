package com.lumyuan.filebrowser.ui.model

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Path
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.view.MotionEventCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.lumyuan.filebrowser.ui.model.PathScroller.PathPointsHolder

class OverScrollDelegate(overScrollable: OverScrollable) {
    private var mState = 0
    private val mTouchSlop: Int
    private var mLastMotionY = 0f
    private var mOffsetY = 0f
    private var mActivePointerId = -1
    private val mScroller: PathScroller
    private val mView: View
    private val mOverScrollable: OverScrollable
    private var mEnableDragOverScroll = true
    private var mEnableFlingOverScroll = true
    private var mStyle: OverScrollStyle
    fun setOverScrollStyle(style: OverScrollStyle?) {
        requireNotNull(style) { "OverScrollStyle should NOT be NULL!" }
        mStyle = style
    }

    fun setOverScrollType(dragOverScroll: Boolean, flingOverScroll: Boolean) {
        mEnableDragOverScroll = dragOverScroll
        mEnableFlingOverScroll = flingOverScroll
    }

    fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return mEnableDragOverScroll && onInterceptTouchEventInternal(event)
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        return mEnableDragOverScroll && onTouchEventInternal(event)
    }

    fun overScrollBy(
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
        val overScroll = mOverScrollable.superOverScrollBy(
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
        if (mEnableFlingOverScroll) {
            if (!overScroll) {
                if (mState == 4) {
                    log("warning, overScroll=flase BUT mState=OS_FLING")
                }
            } else if (!isTouchEvent && mState != 4) {
                log("deltaY->$deltaY")
                val velocityY = -(deltaY.toFloat() / 0.0166666f).toInt()
                onAbsorb(velocityY)
            }
        }
        return overScroll
    }

    fun recyclerViewAbsorbGlows(velocityX: Int, velocityY: Int) {
        log("recyclerViewAbsorbGlows velocityY->$velocityY")
        if (mEnableFlingOverScroll && velocityY != 0) {
            onAbsorb(-velocityY)
        }
    }

    private fun onAbsorb(velocityY: Int) {
        mOffsetY = (if (velocityY > 0) -1 else 1).toFloat()
        val overY = velocityY.toFloat() * 0.07f
        log("velocityY->$velocityY overY->$overY")
        mScroller.start(overY, 550, sFlingBackPathPointsHolder)
        setState(4)
        mView.invalidate()
    }

    fun draw(canvas: Canvas) {
        if (mState == 0) {
            mOverScrollable.superDraw(canvas)
        } else {
            if (mState == 3 || mState == 4) {
                if (mScroller.computeScrollOffset()) {
                    mOffsetY = mScroller.currY.toFloat()
                } else {
                    mOffsetY = 0.0f
                    setState(0)
                }
                ViewCompat.postInvalidateOnAnimation(mView)
            }
            val sc = canvas.save()
            mStyle.transformOverScrollCanvas(mOffsetY, canvas, mView)
            mOverScrollable.superDraw(canvas)
            canvas.restoreToCount(sc)
            val sc1 = canvas.save()
            if (mOffsetY > 0.0f) {
                mStyle.drawOverScrollTop(mOffsetY, canvas, mView)
            } else if (mOffsetY < 0.0f) {
                mStyle.drawOverScrollBottom(mOffsetY, canvas, mView)
            }
            canvas.restoreToCount(sc1)
        }
    }

    private fun setState(newState: Int) {
        if (mState != newState) {
            mState = newState
            var newStateName = ""
            if (mState == 0) {
                newStateName = "OS_NONE"
            } else if (mState == 1) {
                newStateName = "OS_TOP"
            } else if (mState == 2) {
                newStateName = "OS_BOTTOM"
            } else if (mState == 3) {
                newStateName = "OS_SPRING_BACK"
            } else if (mState == 4) {
                newStateName = "OS_FLING"
            }
            log("setState->$newStateName")
        }
    }

    private val isOsTop: Boolean
        private get() = mState == 1
    private val isOsBottom: Boolean
        private get() = mState == 2
    private val isOsDrag: Boolean
        private get() = mState == 1 || mState == 2

    private fun onInterceptTouchEventInternal(event: MotionEvent): Boolean {
        val action = MotionEventCompat.getActionMasked(event)
        when (action) {
            0 -> {
                log("onInterceptTouchEvent -> ACTION_DOWN")
                mLastMotionY = event.y
                mActivePointerId = event.getPointerId(0)
                if (mState == 3 && mScroller.computeScrollOffset()) {
                    mOffsetY = mScroller.currY.toFloat()
                    mScroller.abortAnimation()
                    if (mOffsetY == 0.0f) {
                        setState(0)
                    } else {
                        setState(if (mOffsetY > 0.0f) 1 else 2)
                    }
                    mView.invalidate()
                }
            }
            1, 3 -> mActivePointerId = -1
            2 -> if (mActivePointerId == -1) {
                Log.e(
                    "OverScrollDelegate",
                    "Got ACTION_MOVE event but don't have an active pointer id."
                )
            } else {
                val pointerIndex = MotionEventCompat.findPointerIndex(event, mActivePointerId)
                if (pointerIndex == -1) {
                    Log.e(
                        "OverScrollDelegate",
                        "Invalid pointerId=" + mActivePointerId + " in onInterceptTouchEvent"
                    )
                } else {
                    val y = MotionEventCompat.getY(event, pointerIndex)
                    val yDiff = y - mLastMotionY
                    mLastMotionY = y
                    if (!isOsDrag) {
                        val offset = mOverScrollable.superComputeVerticalScrollOffset()
                        val range =
                            mOverScrollable.superComputeVerticalScrollRange() - mOverScrollable.superComputeVerticalScrollExtent()
                        val canScrollUp: Boolean
                        val canScrollDown: Boolean
                        if (range == 0) {
                            canScrollUp = false
                            canScrollDown = false
                        } else {
                            canScrollUp = offset > 0
                            canScrollDown = offset < range - 1
                        }
                        if ((!canScrollUp || !canScrollDown) && Math.abs(yDiff) > mTouchSlop.toFloat()) {
                            var isOs = false
                            if (!canScrollUp && yDiff > 0.0f) {
                                setState(1)
                                isOs = true
                            } else if (!canScrollDown && yDiff < 0.0f) {
                                setState(2)
                                isOs = true
                            }
                            if (isOs) {
                                val fakeCancelEvent = MotionEvent.obtain(event)
                                fakeCancelEvent.action = 3
                                mOverScrollable.superOnTouchEvent(fakeCancelEvent)
                                fakeCancelEvent.recycle()
                                mOverScrollable.superAwakenScrollBars()
                                val parent = mView.parent
                                parent?.requestDisallowInterceptTouchEvent(true)
                            }
                        }
                    }
                }
            }
            4, 5 -> {}
            6 -> onSecondaryPointerUp(event)
            else -> {}
        }
        return isOsDrag
    }

    private fun onTouchEventInternal(event: MotionEvent): Boolean {
        val action = MotionEventCompat.getActionMasked(event)
        val pointerIndex: Int
        when (action) {
            0 -> {
                log("onTouchEvent -> ACTION_DOWN")
                mLastMotionY = event.y
                mActivePointerId = event.getPointerId(0)
            }
            1, 3 -> {
                if (mOffsetY != 0.0f) {
                    pointerIndex = Math.round(mOffsetY)
                    mScroller.start(pointerIndex.toFloat(), 420, sDragBackPathPointsHolder)
                    setState(3)
                    mView.invalidate()
                }
                mActivePointerId = -1
            }
            2 -> if (mActivePointerId == -1) {
                Log.e(
                    "OverScrollDelegate",
                    "Got ACTION_MOVE event but don't have an active pointer id."
                )
            } else {
                pointerIndex = MotionEventCompat.findPointerIndex(event, mActivePointerId)
                if (pointerIndex < 0) {
                    Log.e(
                        "OverScrollDelegate",
                        "Got ACTION_MOVE event but have an invalid active pointer id."
                    )
                } else {
                    val y = MotionEventCompat.getY(event, pointerIndex)
                    val yDiff = y - mLastMotionY
                    mLastMotionY = y
                    if (!isOsDrag) {
                        val offset = mOverScrollable.superComputeVerticalScrollOffset()
                        val range =
                            mOverScrollable.superComputeVerticalScrollRange() - mOverScrollable.superComputeVerticalScrollExtent()
                        val canScrollUp: Boolean
                        val canScrollDown: Boolean
                        if (range == 0) {
                            canScrollUp = false
                            canScrollDown = false
                        } else {
                            canScrollUp = offset > 0
                            canScrollDown = offset < range - 1
                        }
                        if (canScrollUp && canScrollDown) {
                            return isOsDrag
                        }
                        if (Math.abs(yDiff) >= 1.0f) {
                            var isOs = false
                            if (!canScrollUp && yDiff > 0.0f) {
                                setState(1)
                                isOs = true
                            } else if (!canScrollDown && yDiff < 0.0f) {
                                setState(2)
                                isOs = true
                            }
                            if (isOs) {
                                val fakeCancelEvent = MotionEvent.obtain(event)
                                fakeCancelEvent.action = 3
                                mOverScrollable.superOnTouchEvent(fakeCancelEvent)
                                fakeCancelEvent.recycle()
                                mOverScrollable.superAwakenScrollBars()
                                val parent = mView.parent
                                parent?.requestDisallowInterceptTouchEvent(true)
                            }
                        }
                    }
                    if (isOsDrag) {
                        mOffsetY += yDiff
                        val fakeDownEvent: MotionEvent
                        if (isOsTop) {
                            if (mOffsetY <= 0.0f) {
                                setState(0)
                                mOffsetY = 0.0f
                                fakeDownEvent = MotionEvent.obtain(event)
                                fakeDownEvent.action = 0
                                mOverScrollable.superOnTouchEvent(fakeDownEvent)
                                fakeDownEvent.recycle()
                            }
                        } else if (isOsBottom && mOffsetY >= 0.0f) {
                            setState(0)
                            mOffsetY = 0.0f
                            fakeDownEvent = MotionEvent.obtain(event)
                            fakeDownEvent.action = 0
                            mOverScrollable.superOnTouchEvent(fakeDownEvent)
                            fakeDownEvent.recycle()
                        }
                        mView.invalidate()
                    }
                }
            }
            4 -> {}
            5 -> {
                pointerIndex = MotionEventCompat.getActionIndex(event)
                mLastMotionY = MotionEventCompat.getY(event, pointerIndex)
                mActivePointerId = MotionEventCompat.getPointerId(event, pointerIndex)
            }
            6 -> {
                onSecondaryPointerUp(event)
                pointerIndex = MotionEventCompat.findPointerIndex(event, mActivePointerId)
                if (pointerIndex != -1) {
                    mLastMotionY = MotionEventCompat.getY(event, pointerIndex)
                }
            }
            else -> {}
        }
        return isOsDrag
    }

    private fun onSecondaryPointerUp(ev: MotionEvent) {
        val pointerIndex = MotionEventCompat.getActionIndex(ev)
        val pointerId = MotionEventCompat.getPointerId(ev, pointerIndex)
        if (pointerId == mActivePointerId) {
            val newPointerIndex = if (pointerIndex == 0) 1 else 0
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex)
        }
    }

    private fun log(msg: String) {
        Log.d("OverScrollDelegate", msg)
    }

    abstract class OverScrollStyle {
        companion object {
            const val DEFAULT_DRAW_TRANSLATE_RATE = 0.36f
            var SYSTEM_DENSITY = 0f
            fun dp2px(dp: Int): Int {
                return (dp.toFloat() * SYSTEM_DENSITY + 0.5f).toInt()
            }

            init {
                SYSTEM_DENSITY = Resources.getSystem().displayMetrics.density
            }
        }

        fun transformOverScrollCanvas(offsetY: Float, canvas: Canvas, view: View?) {
            val translateY = Math.round(offsetY * 0.36f)
            canvas.translate(0.0f, translateY.toFloat())
        }

        fun drawOverScrollTop(offsetY: Float, canvas: Canvas?, view: View?) {}
        fun drawOverScrollBottom(offsetY: Float, canvas: Canvas?, view: View?) {}
        fun getOverScrollViewCanvasBottom(view: View): Int {
            return view.height + view.scrollY
        }
    }

    interface OverScrollable {
        fun superComputeVerticalScrollExtent(): Int
        fun superComputeVerticalScrollOffset(): Int
        fun superComputeVerticalScrollRange(): Int
        fun superOnTouchEvent(var1: MotionEvent?)
        fun superDraw(var1: Canvas?)
        fun superAwakenScrollBars(): Boolean
        fun superOverScrollBy(
            var1: Int,
            var2: Int,
            var3: Int,
            var4: Int,
            var5: Int,
            var6: Int,
            var7: Int,
            var8: Int,
            var9: Boolean
        ): Boolean

        val overScrollableView: View
        val overScrollDelegate: OverScrollDelegate?
    }

    companion object {
        private val sDefaultStyle: OverScrollStyle = object : OverScrollStyle() {}
        private val sDragBackPathPointsHolder = buildDragBackPathPointsHolder()
        private val sFlingBackPathPointsHolder = buildFlingBackPathPointsHolder()
        const val LOG_TAG = "OverScrollDelegate"
        const val OS_NONE = 0
        const val OS_DRAG_TOP = 1
        const val OS_DRAG_BOTTOM = 2
        const val OS_SPRING_BACK = 3
        const val OS_FLING = 4
        private const val DRAG_BACK_DURATION = 420
        private const val FLING_BACK_DURATION = 550
        private const val INVALID_POINTER = -1
        private fun buildDragBackPathPointsHolder(): PathPointsHolder {
            val startY = 1.0f
            val path = Path()
            path.moveTo(0.0f, 1.0f)
            path.cubicTo(0.11f, 0.11f, 0.36f, 0.05f, 1.0f, 0.0f)
            return PathPointsHolder(path)
        }

        private fun buildFlingBackPathPointsHolder(): PathPointsHolder {
            val baseOverY = 1.0f
            val path = Path()
            path.moveTo(0.0f, 0.0f)
            path.cubicTo(0.05f, 0.8f, 0.09f, 1.2f, 0.21f, 0.88f)
            path.cubicTo(0.48f, 0.1f, 0.72f, 0.02f, 1.0f, 0.0f)
            return PathPointsHolder(path)
        }
    }

    init {
        mView = overScrollable.overScrollableView
        if (mView is RecyclerView) {
            mView.setOverScrollMode(0)
        } else {
            mView.overScrollMode = 2
        }
        mOverScrollable = overScrollable
        val context = mView.context
        mScroller = PathScroller()
        val configuration = ViewConfiguration.get(context)
        mTouchSlop = configuration.scaledTouchSlop / 2
        mStyle = sDefaultStyle
    }
}
