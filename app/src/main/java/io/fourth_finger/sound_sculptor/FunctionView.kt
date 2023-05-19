package io.fourth_finger.sound_sculptor

import android.content.Context
import android.graphics.Canvas
import android.view.View
import kotlin.math.roundToInt
import kotlin.properties.Delegates


/**
 * A Surface view that draws graphs that correspond to the envelopes on the NDK side.
 * setPosition must be called before this view is drawn.
 */
class FunctionView(context: Context): View(context) {

    private var row: Int by Delegates.notNull()
    private var col: Int by Delegates.notNull()

    private external fun getXToYRatio(
        row: Int,
        col: Int
    ) : Double

    private external fun nativeDraw(
        canvas: Canvas,
        row: Int,
        col: Int
    )

    private val xToYRatio: Double
        get() { return getXToYRatio(row, col) }

    // TODO this is a bad solution
    fun setPosition(row: Int, col: Int){
        this.row = row
        this.col = col
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        nativeDraw(canvas, row, col)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Want to match height
        val measuredHeight = MeasureSpec.getSize(heightMeasureSpec)
        val desiredWidth = (measuredHeight * xToYRatio).roundToInt()

        // Sacrifice width if needed
        val width = if (desiredWidth > MeasureSpec.getSize(widthMeasureSpec)) {
            resolveSizeAndState(
                desiredWidth,
                widthMeasureSpec,
                measuredState
            ) or MEASURED_SIZE_MASK
        } else {
            desiredWidth
        }

        setMeasuredDimension(width, measuredHeight)
    }

}