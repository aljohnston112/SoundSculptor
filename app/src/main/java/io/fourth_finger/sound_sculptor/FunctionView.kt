package io.fourth_finger.sound_sculptor

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import kotlin.math.roundToInt
import kotlin.properties.Delegates


/**
 * A Surface view that draws graphs that correspond to the envelopes on the NDK side.
 * setPosition must be called before this view is drawn.
 */
class FunctionView(
    context: Context,
    attributeSet: AttributeSet
): SurfaceView(context, attributeSet) {

    private var row: Int by Delegates.notNull()
    private var col: Int by Delegates.notNull()

    private external fun getXToYRatio(
        row: Int,
        col: Int
    ) : Double

    private external fun nativeDraw(
        surface: Surface,
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

    fun draw() {
        holder.addCallback( object :  SurfaceHolder.Callback {
            override fun surfaceCreated(p0: SurfaceHolder) {
                nativeDraw(holder.surface, row, col)
            }

            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
                nativeDraw(holder.surface, row, col)
            }

            override fun surfaceDestroyed(p0: SurfaceHolder) {

            }
        })
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