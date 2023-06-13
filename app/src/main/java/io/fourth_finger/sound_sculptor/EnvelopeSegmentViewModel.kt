package io.fourth_finger.sound_sculptor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class EnvelopeSegmentViewModel(
    private val envelopeSegmentsUseCase: EnvelopeSegmentsUseCase
) : ViewModel() {


    fun newEnvelopeData(
        isAmplitude: Boolean,
        function: String?,
        start: Double?,
        end: Double?,
        time: Double?
    ) {
        if (start != null && end != null && time != null && function != null) {
            val functions = arrayOf(EnvelopeArguments.getFunctionType(function))
            val functionArgs = arrayOf(
                doubleArrayOf(
                    start,
                    end,
                    time
                )
            )
            if (isAmplitude) {
                addAmplitudeEnvelopeSegment(
                    functions,
                    functionArgs
                )
                envelopeSegmentsUseCase.addAmplitudeEnvelopeSegment(
                    EnvelopeArguments(functions, functionArgs)
                )
            } else {
                addFrequencyEnvelopeSegment(
                    functions,
                    functionArgs
                )
                EnvelopeArguments(functions, functionArgs)
                envelopeSegmentsUseCase.addFrequencyEnvelopeSegment(
                    EnvelopeArguments(functions, functionArgs)
                )
            }
        }
    }

}

class EnvelopeSegmentViewModelFactory(
    private val envelopeSegmentsUseCase: EnvelopeSegmentsUseCase
    ) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EnvelopeSegmentViewModel::class.java)) {
            return EnvelopeSegmentViewModel(envelopeSegmentsUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}