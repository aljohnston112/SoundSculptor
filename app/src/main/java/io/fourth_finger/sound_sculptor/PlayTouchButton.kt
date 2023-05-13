package io.fourth_finger.sound_sculptor

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.Button
import androidx.appcompat.widget.AppCompatButton

class PlayTouchButton(context: Context, attrs: AttributeSet) : AppCompatButton(context, attrs) {
    private external fun startPlaying()
    private external fun triggerRelease()

    var downTouch = false

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)

        // Listening for the down and up touch events.
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downTouch = true
                startPlaying();
                true
            }

            MotionEvent.ACTION_UP -> if (downTouch) {
                downTouch = false
                performClick() // Call this method to handle the response and
                // enable accessibility services to
                // perform this action for a user who can't
                // tap the touchscreen.
                true
            } else {
                false
            }

            else -> false  // Return false for other touch events.
        }
    }

    override fun performClick(): Boolean {
        // Calls the super implementation, which generates an AccessibilityEvent
        // and calls the onClick() listener on the view, if any.
        super.performClick()

        triggerRelease()

        return true
    }
}
