package io.fourth_finger.sound_sculptor.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.fourth_finger.sound_sculptor.data_class.Envelope
import io.fourth_finger.sound_sculptor.data_class.EnvelopeArguments
import io.fourth_finger.sound_sculptor.EnvelopeRepository
import io.fourth_finger.sound_sculptor.EnvelopeSegmentCache
import io.fourth_finger.sound_sculptor.addAmplitudeEnvelopeSegmentArguments
import io.fourth_finger.sound_sculptor.addFrequencyEnvelopeSegmentArguments
import io.fourth_finger.sound_sculptor.init
import kotlinx.coroutines.launch
import java.nio.file.FileAlreadyExistsException

class EnvelopeViewModel(
    private val envelopeRepository: EnvelopeRepository
) : ViewModel() {

    private var envelopeSegmentCache: EnvelopeSegmentCache = EnvelopeSegmentCache()

    /**
     * Loads an envelope from the repository and sends it to the JNI.
     * TODO The recycler view adapter should pick up on these changes
     *
     * @param envelopeName The name of the envelope to load.
     * @param onEnvelopeLoaded Called when the envelope has been loaded and is ready to display.
     *                         The boolean parameter represents whether the envelope was loaded or not.
     */
    fun loadEnvelope(context: Context, envelopeName: String, onEnvelopeLoaded: (Boolean) -> Unit) {
        viewModelScope.launch {
            init()
            
            val envelope = envelopeRepository.loadEnvelope(context, envelopeName)
            val amplitudeArguments = envelope?.amplitudeEnvelopeArguments
            val frequencyArguments = envelope?.frequencyEnvelopeArguments

            var loaded = false
            if (amplitudeArguments != null && frequencyArguments != null) {
                loadArguments(amplitudeArguments, frequencyArguments)
                loaded = true
            }
            
            onEnvelopeLoaded(loaded)
        }
    }

    /**
     * Loads an envelope into the JNI and the envelope segments use case.
     * 
     * @param amplitudeArguments The amplitude envelope segment arguments of the envelope.
     * @param frequencyArguments The frequency envelope segment arguments of the envelope.
     */
    private fun loadArguments(
        amplitudeArguments: List<EnvelopeArguments>,
        frequencyArguments: List<EnvelopeArguments>
    ) {
        envelopeSegmentCache = EnvelopeSegmentCache()

        for (amplitudeArgument in amplitudeArguments) {
            envelopeSegmentCache.addAmplitudeEnvelopeSegmentArguments(amplitudeArgument)
            addAmplitudeEnvelopeSegmentArguments(
                amplitudeArgument.functionType,
                amplitudeArgument.functionArguments
            )

        }

        for (frequencyArgument in frequencyArguments) {
            envelopeSegmentCache.addFrequencyEnvelopeSegmentArguments(frequencyArgument)
            addFrequencyEnvelopeSegmentArguments(
                frequencyArgument.functionType,
                frequencyArgument.functionArguments
            )
        }
        
    }

    /**
     * Tries to save the current envelope with the given name.
     *
     * @param name The name to save the envelope under.
     */
    fun trySave(context: Context, name: String?) {
        val names = envelopeRepository.envelopeNames.value ?: setOf()
        if (name != null && !names.contains(name)) {
            val amplitudeEnvelope = envelopeSegmentCache.getAmplitudeEnvelopeArguments()
            val frequencyEnvelope = envelopeSegmentCache.getFrequencyEnvelopeArguments()
            viewModelScope.launch {
                val envelope = Envelope(name, amplitudeEnvelope, frequencyEnvelope)
                envelopeRepository.saveEnvelope(context, envelope)
            }
        } else {
            throw FileAlreadyExistsException("Envelope named $name already exists")
        }

    }

    /**
     * Used to notify of new envelope segment data.
     *
     * @param isAmplitude True if the envelope segment should be for the amplitude envelope;
     *                    False if it is for the frequency envelope.
     * @param function The name of the function used to generate the envelope.
     * @param functionArgs The arguments for the function.
     */
    fun newEnvelopeData(
        isAmplitude: Boolean,
        function: String,
        functionArgs: DoubleArray
    ) {
        // The envelope data needs to be added to the use case and
        // sent through the JNI for envelope generation
        val functions = arrayOf(EnvelopeArguments.getFunctionType(function))
        if (isAmplitude) {
            addAmplitudeEnvelopeSegmentArguments(
                functions,
                arrayOf(functionArgs)
            )
            envelopeSegmentCache.addAmplitudeEnvelopeSegmentArguments(
                EnvelopeArguments(functions, arrayOf(functionArgs))
            )
        } else {
            addFrequencyEnvelopeSegmentArguments(
                functions,
                arrayOf(functionArgs)
            )
            EnvelopeArguments(functions, arrayOf(functionArgs))
            envelopeSegmentCache.addFrequencyEnvelopeSegmentArguments(
                EnvelopeArguments(functions, arrayOf(functionArgs))
            )
        }
    }

}

class EnvelopeViewModelFactory(
    private val envelopeRepository: EnvelopeRepository,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EnvelopeViewModel::class.java)) {
            return EnvelopeViewModel(
                envelopeRepository,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}