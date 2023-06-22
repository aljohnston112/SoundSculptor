package io.fourth_finger.sound_sculptor.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import io.fourth_finger.sound_sculptor.R
import io.fourth_finger.sound_sculptor.data_class.Envelope
import io.fourth_finger.sound_sculptor.data_class.MinMax
import io.fourth_finger.sound_sculptor.data_class.MinMaxObserver
import io.fourth_finger.sound_sculptor.data_class.MinMaxSubject
import io.fourth_finger.sound_sculptor.fillBufferWithGraph
import io.fourth_finger.sound_sculptor.getMinMax
import io.fourth_finger.sound_sculptor.getSeconds
import io.fourth_finger.sound_sculptor.isValidPosition
import io.fourth_finger.sound_sculptor.util.drawBorder
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.properties.Delegates

/**
 * A view that draws a single envelope segment.
 */
class FunctionView(
    context: Context,
    attributeSet: AttributeSet
) : MainView(context, attributeSet), MinMaxObserver {

    private var envelopeType: Envelope.EnvelopeType by Delegates.notNull()
    private var column: Int by Delegates.notNull()

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val borderStrokeWidth = 8

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * Contains the points to draw between
     */
    private var directFloatBuffer: FloatBuffer = ByteBuffer.allocateDirect(
        width * 4
    ).order(ByteOrder.nativeOrder()).asFloatBuffer()

    /**
     * Contains the envelope drawing
     */
    private val envelopePath = Path()

    private lateinit var minMax: MinMax

    init {
        borderPaint.strokeWidth = borderStrokeWidth.toFloat()
        borderPaint.color = Color.BLACK

        linePaint.color = context.getColor(R.color.purple_200)
        linePaint.strokeWidth = 8f

        textPaint.textSize = 64f
        textPaint.color = Color.BLACK
    }

    fun setTypeAndColumn(envelopeType: Envelope.EnvelopeType, column: Int){
        this.envelopeType = envelopeType
        this.column = column
    }

    /**
     * Called when a new minMax is available.
     *
     * @param minMax The updated min max.
     */
    override fun update(minMax: MinMax) {
        if (!::minMax.isInitialized || this.minMax != minMax) {
            this.minMax = minMax
            fillBuffer()
        }
    }

    /**
     * Fills the underlying buffer with the envelope segment graph.
     */
    private fun fillBuffer() {
        if (width > 0) {
            envelopePath.rewind()
            // Load the envelope points into a buffer
            if (directFloatBuffer.capacity() != width * 4) {
                directFloatBuffer = ByteBuffer.allocateDirect(
                    width * 4
                ).order(ByteOrder.nativeOrder()).asFloatBuffer()
            }
            fillBufferWithGraph(
                directFloatBuffer,
                envelopeType,
                column,
                width
            )

            // Draw the path
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

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        // Want to keep height
        val measuredHeight = MeasureSpec.getSize(heightMeasureSpec)

        // Sacrifice width if needed
        val xToYRatio = getSeconds(envelopeType, column)
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
    }


    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(envelopePath, linePaint)
        drawYValues(
            canvas,
            directFloatBuffer[0],
            directFloatBuffer[directFloatBuffer.capacity() - 1]
        )
        drawBorder(canvas)
    }

    private fun drawYValues(canvas: Canvas, startValue: Float, endValue: Float) {
        val startText: String = String.format("(%.2f)", startValue)
        val endText: String = String.format("(%.2f)", endValue)

        // The text should only take up to half the view width
        var textWidthStart = textPaint.measureText(startText)
        var textWidthEnd = textPaint.measureText(endText)
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

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        fillBuffer()
    }

}