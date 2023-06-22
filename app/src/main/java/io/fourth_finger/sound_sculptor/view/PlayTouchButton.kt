package io.fourth_finger.sound_sculptor.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatButton
import io.fourth_finger.sound_sculptor.startPlaying
import io.fourth_finger.sound_sculptor.triggerRelease

/**
 * Used to play the current envelope.
 */
class PlayTouchButton(
    context: Context,
    attrs: AttributeSet
) : AppCompatButton(context, attrs) {

    var downTouch = false

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)

        return when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                downTouch = true
                startPlaying()
                true
            }

            MotionEvent.ACTION_UP -> if (downTouch) {
                downTouch = false
                performClick()
                true
            } else {
                false
            }

            else -> false
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        triggerRelease()
        return true
    }

}
