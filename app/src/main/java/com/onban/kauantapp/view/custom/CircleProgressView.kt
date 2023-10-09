package com.onban.kauantapp.view.custom

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.onban.kauantapp.R

/*
https://medium.com/@paulnunezm/canvas-animations-simple-circle-progress-view-on-android-8309900ab8ed
 */
class CircleProgressView : View {
    constructor(context: Context?) : super(context) {
        setWillNotDraw(false)
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    companion object {
        const val PERCENTAGE_MAX = 360
        const val PERCENTAGE_DIVIDER = 100.0
        const val PERCENTAGE_VALUE_HOLDER = "percentage"
    }

    private val ovalSpace = RectF()

    private val backgroundArcColor = context.resources?.getColor(R.color.gray, null) ?: Color.GRAY
    private val fillArcColor = context.resources?.getColor(R.color.eastsoft_back, null) ?: Color.BLUE

    private var currentPercentage = 0
    private var percentage = 0

    fun update(percent: Int) {
        currentPercentage = 0
        percentage = percent
        animator.cancel()
        animateProgress()
    }

    private val backgroundArcPaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
        color = backgroundArcColor
        strokeWidth = 20f
    }

    private val fillArcPaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
        color = fillArcColor
        strokeWidth = 20f
        strokeCap = Paint.Cap.ROUND
    }

    private val animator = ValueAnimator()

    private fun setOvalSpace() {
        val horizontalCenter = (width.div(2)).toFloat()
        val verticalCenter = (height.div(2)).toFloat()
        val ovalSize = measuredWidth * 0.3f
        ovalSpace.set(
            horizontalCenter - ovalSize,
            verticalCenter - ovalSize,
            horizontalCenter + ovalSize,
            verticalCenter + ovalSize
        )
    }

    override fun onDraw(canvas: Canvas?) {
        setOvalSpace()
        canvas?.let {
            drawBackgroundArc(it)
            drawInnerArc(it)
        }
    }
    private fun drawBackgroundArc(it: Canvas) {
        it.drawArc(ovalSpace, 0f, 360f, false, backgroundArcPaint)
    }

    private fun drawInnerArc(canvas: Canvas) {
        val percentageToFill = getCurrentPercentageToFill()
        canvas.drawArc(ovalSpace, 270f, percentageToFill, false, fillArcPaint)
    }

    private fun getCurrentPercentageToFill() =
        (PERCENTAGE_MAX * (currentPercentage / PERCENTAGE_DIVIDER)).toFloat()

    private fun animateProgress() {
        val valuesHolder = PropertyValuesHolder.ofInt(PERCENTAGE_VALUE_HOLDER, 0, percentage)
        animator.apply {
            setValues(valuesHolder)
            duration = 1000
            addUpdateListener {
                val percentage = it.getAnimatedValue(PERCENTAGE_VALUE_HOLDER) as Int
                currentPercentage = percentage
                invalidate()
            }
        }
        animator.start()
    }
}
