package io.fourth_finger.sound_sculptor

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class EnvelopeViewModel(
    private val envelopeRepository: EnvelopeRepository
) : ViewModel() {

    private fun loadEnvelopeData(context: Context, name: String) {
        viewModelScope.launch {
            val envelopeData = envelopeRepository.getEnvelopeData(context, name)
            // TODO Do something with the data
        }
    }

    private fun saveEnvelopeData(
        context: Context,
        envelopeData: List<EnvelopeData>,
        name: String
    ) {
        viewModelScope.launch {
            envelopeRepository.saveEnvelopeData(context, envelopeData, name)
        }
    }

}