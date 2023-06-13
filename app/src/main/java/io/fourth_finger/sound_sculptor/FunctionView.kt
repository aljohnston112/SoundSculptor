package io.fourth_finger.sound_sculptor

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.properties.Delegates

/**
 * A Surface view that draws graphs that represent the
 * envelope_segments on the NDK side.
 * [updateMinMax] must be called before this view is drawn.
 */
class FunctionView(
    context: Context,
    attributeSet: AttributeSet
) : MainView(context, attributeSet), MinMaxObserver {

    private var envelopeType: Envelope.EnvelopeType by Delegates.notNull()
    private var column: Int by Delegates.notNull()

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val borderStrokeWidth = 8;

    private var directFloatBuffer: FloatBuffer = ByteBuffer.allocateDirect(
        width * 4
    ).order(ByteOrder.nativeOrder()).asFloatBuffer()
    private val envelopePath = Path()

    private lateinit var minMaxSubject: MinMaxSubject
    private lateinit var minMax: MinMax

    init {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        linePaint.color = context.getColor(R.color.purple_200)
        linePaint.strokeWidth = 8f
        linePaint.strokeCap = Paint.Cap.ROUND

        borderPaint.strokeWidth = borderStrokeWidth.toFloat()
        borderPaint.color = Color.BLACK


        textPaint.textSize = 64f
        textPaint.color = Color.BLACK
    }

    private val xToYRatio: Double
        get() {
            return getSeconds(envelopeType, column)
        }

    // TODO is this a bad solution?
    fun update(
        envelopeType: Envelope.EnvelopeType,
        column: Int,
        minMaxSubject: MinMaxSubject?
    ) {
        this.envelopeType = envelopeType
        this.column = column
        if (!::minMaxSubject.isInitialized && minMaxSubject != null){
                this.minMaxSubject = minMaxSubject
        }
        if (hasValidPosition() && width > 0) {
            directFloatBuffer = ByteBuffer.allocateDirect(
                width * 4
            ).order(ByteOrder.nativeOrder()).asFloatBuffer()
            envelopePath.rewind()
            val minMax = getGraph(
                directFloatBuffer,
                envelopeType,
                column,
                width
            )
            if(!::minMax.isInitialized) {
                this.minMax = MinMax(minMax[0], minMax[1])
            }
            this.minMaxSubject.setMinMax(this.minMax)

            val minY = this.minMax.min
            val maxY = this.minMax.max

            val yScale: Float = height / (maxY - minY)
            val firstValue = abs(
                (directFloatBuffer[0] - minY) - (maxY - minY)
            ) * yScale
            envelopePath.moveTo(0F, firstValue)
            for (i in 1 until directFloatBuffer.capacity()) {
                val currentValue = abs(
                    (directFloatBuffer[i] - minY) - (maxY - minY)
                ) * yScale
                envelopePath.lineTo(i.toFloat(), currentValue)
            }
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (hasValidPosition()) {
            canvas.drawPath(envelopePath, linePaint)
            drawYValues(
                canvas,
                directFloatBuffer[0],
                directFloatBuffer[directFloatBuffer.capacity() - 1]
            )
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

    private fun hasValidPosition(): Boolean {
        return isValidPosition(envelopeType, column)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Want to keep height
        val measuredHeight = MeasureSpec.getSize(heightMeasureSpec)

        if (hasValidPosition()) {
            // Sacrifice width if needed
            val desiredWidth = (measuredHeight * xToYRatio).roundToInt()
            var width = if (
                desiredWidth > MeasureSpec.getSize(widthMeasureSpec) &&
                MeasureSpec.getSize(widthMeasureSpec) != 0
            ) {
                // TODO why does this return a value greater than desiredWidth?
                resolveSizeAndState(
                    desiredWidth,
                    widthMeasureSpec,
                    measuredState
                ) or MEASURED_SIZE_MASK
            } else {
                desiredWidth
            }

            if (width > desiredWidth) {
                width = desiredWidth
            }
            setMeasuredDimension(width, measuredHeight)
        } else {
            setMeasuredDimension(measuredHeight, measuredHeight)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        update(envelopeType, column, null)
        invalidate()
    }

    private fun updateMinMax(minMax: MinMax) {
        if(this.minMax != minMax){
            this.minMax = minMax
            update(envelopeType, column, null)
            invalidate()
        }
    }

    override fun update(minMax: MinMax) {
        updateMinMax(minMax)
    }

}