package io.fourth_finger.sound_sculptor

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import io.fourth_finger.sound_sculptor.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private external fun init(
        functionEnumsAmplitude: Array<Envelope.FunctionType>,
        functionArgsAmplitude: Array<DoubleArray>,
        functionEnumsFrequency: Array<Envelope.FunctionType>,
        functionArgsFrequency: Array<DoubleArray>
    )

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )

        val functionEnums = Array(3) { Envelope.FunctionType.LINEAR }
        val functionArgsFrequency = arrayOf(
            doubleArrayOf(200.0, 400.0, 1.0),
            doubleArrayOf(400.0, 400.0, 0.0),
            doubleArrayOf(400.0, 20.0, 1.0),
        )
        val functionArgsAmplitude = arrayOf(
            doubleArrayOf(0.0, 1.0, 1.0),
            doubleArrayOf(1.0, 1.0, 0.0),
            doubleArrayOf(1.0, 0.0, 1.0),
            )
        init(functionEnums, functionArgsFrequency, functionEnums, functionArgsAmplitude)
    }

    companion object {
        // Used to load the 'sound_sculptor' library on application startup.
        init {
            System.loadLibrary("sound_sculptor")
        }
    }
}