package io.fourth_finger.sound_sculptor

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

fun drawBorder(canvas: Canvas) {
//        if (isThisSelected) {
//            borderStrokeWidth *= 4
//            borderColor.strokeWidth = borderStrokeWidth.toFloat()
//        }

    val borderStrokeWidth = 8;
    val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    borderPaint.strokeWidth = borderStrokeWidth.toFloat()
    borderPaint.color = Color.BLACK

    val width = canvas.width
    val height = canvas.height

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