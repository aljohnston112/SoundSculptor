package io.fourth_finger.sound_sculptor

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.roundToInt

open class MainView(
    context: Context,
    attributeSet: AttributeSet
) : View(context, attributeSet)

class AddFunctionView(
    context: Context,
    attributeSet: AttributeSet
) : MainView(context, attributeSet) {

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        linePaint.color = context.getColor(R.color.purple_200)
        linePaint.strokeWidth = 8f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawPlus(canvas)
        drawBorder(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Want a square
        val measuredHeight = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(measuredHeight, measuredHeight)
    }


    private fun drawPlus(canvas: Canvas) {
        canvas.drawRect(
            (3.5 * width / 8.0).roundToInt().toFloat(),
            (2.0 * width / 8.0).roundToInt().toFloat(),
            (4.5 * width / 8.0).roundToInt().toFloat(),
            (6.0 * width / 8.0).roundToInt().toFloat(),
            linePaint
        )
        canvas.drawRect(
            (2.0 * width / 8.0).roundToInt().toFloat(),
            (3.5 * width / 8.0).roundToInt().toFloat(),
            (6.0 * width / 8.0).roundToInt().toFloat(),
            (4.5 * width / 8.0).roundToInt().toFloat(),
            linePaint
        )
    }

}