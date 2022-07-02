package com.lumyuan.filebrowser.ui.model

import android.graphics.Path
import android.graphics.PathMeasure
import android.view.animation.AnimationUtils

class PathScroller {
    private var mStartTime: Long = 0
    private var mDuration = 0
    var isFinished = false
        private set
    var currY = 0
        private set
    private var mValueFactor = 0f
    private var mPathPointsHolder: PathPointsHolder? = null
    fun start(valueFactor: Float, duration: Int, pathPointsHolder: PathPointsHolder?) {
        mValueFactor = valueFactor
        mDuration = duration
        isFinished = false
        mPathPointsHolder = pathPointsHolder
        mStartTime = AnimationUtils.currentAnimationTimeMillis()
    }

    fun computeScrollOffset(): Boolean {
        return if (isFinished) {
            false
        } else {
            val timePassed = AnimationUtils.currentAnimationTimeMillis() - mStartTime
            var timePassedPercent = timePassed.toFloat() / mDuration.toFloat()
            if (timePassed >= mDuration.toLong()) {
                isFinished = true
                timePassedPercent = 1.0f
            }
            currY = Math.round(mValueFactor * mPathPointsHolder!!.getY(timePassedPercent))
            true
        }
    }

    fun abortAnimation() {
        isFinished = true
    }

    class PathPointsHolder(path: Path?) {
        private val mX: FloatArray
        private val mY: FloatArray
        fun getY(x: Float): Float {
            return if (x <= 0.0f) {
                mY[0]
            } else if (x >= 1.0f) {
                mY[mY.size - 1]
            } else {
                var startIndex = 0
                var endIndex = mX.size - 1
                while (endIndex - startIndex > 1) {
                    val midIndex = (startIndex + endIndex) / 2
                    if (x < mX[midIndex]) {
                        endIndex = midIndex
                    } else {
                        startIndex = midIndex
                    }
                }
                val xRange = mX[endIndex] - mX[startIndex]
                if (xRange == 0.0f) {
                    mY[startIndex]
                } else {
                    val tInRange = x - mX[startIndex]
                    val fraction = tInRange / xRange
                    val startY = mY[startIndex]
                    val endY = mY[endIndex]
                    startY + fraction * (endY - startY)
                }
            }
        }

        companion object {
            private const val PRECISION = 0.002f
        }

        init {
            val initStart = AnimationUtils.currentAnimationTimeMillis()
            val pathMeasure = PathMeasure(path, false)
            val pathLength = pathMeasure.length
            val numPoints = (pathLength / 0.002f).toInt() + 1
            mX = FloatArray(numPoints)
            mY = FloatArray(numPoints)
            val position = FloatArray(2)
            for (i in 0 until numPoints) {
                val distance = i.toFloat() * pathLength / (numPoints - 1).toFloat()
                pathMeasure.getPosTan(distance, position, null as FloatArray?)
                mX[i] = position[0]
                mY[i] = position[1]
            }
            val usedTime = AnimationUtils.currentAnimationTimeMillis() - initStart
        }
    }

    companion object {
        private const val LOG_TAG = "PathScroller"
        private const val DEBUG = false
    }
}
