package io.fourth_finger.sound_sculptor

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class EnvelopeViewModel(private val envelopeRepository: EnvelopeRepository) : ViewModel() {

    fun getEnvelopeData(context: Context, name: String) {
        viewModelScope.launch {
            val envelopeData = envelopeRepository.getEnvelopeData(context, name)
            // Handle the retrieved envelope data, update the UI, etc.
        }
    }

    fun saveEnvelopeData(context: Context, envelopeData: EnvelopeData, name: String) {
        viewModelScope.launch {
            envelopeRepository.saveEnvelopeData(context, envelopeData, name)
            // Handle the successful save, update the UI, etc.
        }
    }

    fun deleteEnvelopeData(context: Context, name: String) {
        viewModelScope.launch {
            envelopeRepository.deleteEnvelopeData(context, name)
            // Handle the successful deletion, update the UI, etc.
        }
    }
}