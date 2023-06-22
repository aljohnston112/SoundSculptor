package io.fourth_finger.sound_sculptor

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import io.fourth_finger.sound_sculptor.databinding.ActivityMainBinding

/**
 * The main activity hosting the main app fragments
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        init()
    }

    companion object {
        init {
            System.loadLibrary("sound_sculptor")
        }
    }

}