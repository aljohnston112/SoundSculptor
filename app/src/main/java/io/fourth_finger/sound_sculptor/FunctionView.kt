package io.fourth_finger.sound_sculptor

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.properties.Delegates

/**
 * A Surface view that draws graphs that represent the
 * envelopes on the NDK side.
 * [update] must be called before this view is drawn.
 */
class FunctionView(
    context: Context,
    attributeSet: AttributeSet
) : View(context, attributeSet) {

    // TODO split this into two View classes.

    private var envelopeType: Envelope.EnvelopeType by Delegates.notNull()
    private var col: Int by Delegates.notNull()

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val borderStrokeWidth = 8;

    private var directFloatBuffer: FloatBuffer = ByteBuffer.allocateDirect(
        width * 4
    ).order(ByteOrder.nativeOrder()).asFloatBuffer()
    private val envelopePath = Path()

    init {
        linePaint.color = context.getColor(R.color.purple_200)
        linePaint.strokeWidth = 8f

        val borderStrokeWidth = 4
        borderPaint.strokeWidth = borderStrokeWidth.toFloat()
        borderPaint.color = Color.BLACK


        textPaint.textSize = 64f
        textPaint.color = Color.BLACK
    }

    private external fun getSeconds(
        envelopeType: Envelope.EnvelopeType,
        col: Int
    ): Double

    private external fun getGraph(
        byteBuffer: Buffer,
        envelopeType: Envelope.EnvelopeType,
        col: Int,
        width: Int,
        height: Int
    ): FloatArray

    private external fun isValidPosition(
        envelopeType: Envelope.EnvelopeType,
        col: Int
    ): Boolean

    private val xToYRatio: Double
        get() {
            return getSeconds(envelopeType, col)
        }

    // TODO this is a bad solution
    fun update(envelopeType: Envelope.EnvelopeType, col: Int) {
        this.envelopeType = envelopeType
        this.col = col
        if(hasValidPosition() && width > 0) {
            directFloatBuffer = ByteBuffer.allocateDirect(
                width * 4
            ).order(ByteOrder.nativeOrder()).asFloatBuffer()
            envelopePath.rewind()
            val minMax = getGraph(directFloatBuffer, envelopeType, col, width, height)
            val minY = minMax[0]
            val maxY = minMax[1]
            val yScale: Float = height / (maxY - minY)
            val firstValue = abs((directFloatBuffer[0] - minY) - (maxY - minY)) * yScale
            envelopePath.moveTo(0F, firstValue)
            for (i in 1 until directFloatBuffer.capacity()) {
                val currentValue = abs((directFloatBuffer[i] - minY) - (maxY - minY)) * yScale
                envelopePath.lineTo(i.toFloat(), currentValue)
            }
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        if(hasValidPosition()){
            canvas.drawPath(envelopePath, linePaint)
            drawYValues(
                canvas,
                directFloatBuffer[0],
                directFloatBuffer[directFloatBuffer.capacity() - 1]
            )
        }
        else if(!hasValidPosition()){
            drawPlus(canvas)
        }
        drawBorder(canvas)
    }

    private fun drawYValues(canvas: Canvas, startValue: Float, endValue: Float) {
        // Draw the y values for the end points
        val startText: String = String.format("(%.2f)", startValue)
        val endText: String = String.format("(%.2f)", endValue)

        var textWidthStart = textPaint.measureText(startText)
        var textWidthEnd = textPaint.measureText(endText)
        // The text should only take up to half the view width
        while ((textWidthStart + textWidthEnd + borderStrokeWidth) * 2.0 > width) {
            textPaint.textSize = textPaint.textSize - 1
            textWidthStart = textPaint.measureText(startText)
            textWidthEnd = textPaint.measureText(endText)
        }
        canvas.drawText(
            startText,
            (borderStrokeWidth / 2.0).roundToInt().toFloat(),
            (height / 2.0).roundToInt().toFloat(),
            textPaint
        )
        canvas.drawText(
            endText,
            (width - textWidthEnd - borderStrokeWidth / 2.0).roundToInt().toFloat(),
            (height / 2.0).roundToInt().toFloat(),
            textPaint
        )
    }

    private fun drawBorder(canvas: Canvas) {
//        if (isThisSelected) {
//            borderStrokeWidth *= 4
//            borderColor.strokeWidth = borderStrokeWidth.toFloat()
//        }

        //Right
        canvas.drawLine(
            width.toFloat(),
            0f,
            width.toFloat(),
            height.toFloat(),
            borderPaint
        )
        //Left
        canvas.drawLine(0f, height.toFloat(), 0f, 0f, borderPaint)

        // TODO why?
        // Bottom
//        if (isAmplitude) {
//            borderColor.strokeWidth = (borderStrokeWidth / 2.0).toFloat()
//        }
        canvas.drawLine(
            width.toFloat(),
            height.toFloat(),
            0f,
            height.toFloat(),
            borderPaint
        )

        // TODO why?
        //Top
//        if (!isAmplitude) {
//            borderColor.strokeWidth = (borderStrokeWidth / 2.0).toFloat()
//        } else {
//            borderColor.strokeWidth = borderStrokeWidth.toFloat()
//        }

        canvas.drawLine(0f, 0f, width.toFloat(), 0f, borderPaint)
//        if (isThisSelected) {
//            borderStrokeWidth /= 4
//            borderColor.strokeWidth = borderStrokeWidth.toFloat()
//        }
    }

    private fun hasValidPosition(): Boolean {
        return isValidPosition(envelopeType, col)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Want to keep height
        val measuredHeight = MeasureSpec.getSize(heightMeasureSpec)

        if(hasValidPosition()) {
            // Sacrifice width if needed
            val desiredWidth = (measuredHeight * xToYRatio).roundToInt()
            val width = if (desiredWidth > MeasureSpec.getSize(widthMeasureSpec) &&
                MeasureSpec.getSize(widthMeasureSpec) != 0
            ) {
                resolveSizeAndState(
                    desiredWidth,
                    widthMeasureSpec,
                    measuredState
                ) or MEASURED_SIZE_MASK
            } else {
                desiredWidth
            }

            setMeasuredDimension(width, measuredHeight)
        } else {
            setMeasuredDimension(measuredHeight, measuredHeight)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        update(envelopeType, col)
        invalidate()
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