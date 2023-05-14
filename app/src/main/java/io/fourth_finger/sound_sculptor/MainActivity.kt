package io.fourth_finger.sound_sculptor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.fourth_finger.sound_sculptor.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    companion object {
        // Used to load the 'sound_sculptor' library on application startup.
        init {
            System.loadLibrary("sound_sculptor")
        }
    }
}