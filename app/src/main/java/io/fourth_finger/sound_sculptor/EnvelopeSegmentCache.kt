package io.fourth_finger.sound_sculptor

class EnvelopeSegmentCache {

    private val amplitudeEnvelopeSegments = mutableListOf<EnvelopeArguments>()
    private val frequencyEnvelopeSegments = mutableListOf<EnvelopeArguments>()

    fun addAmplitudeEnvelopeSegment(envelopeArguments: EnvelopeArguments) {
        amplitudeEnvelopeSegments.add(envelopeArguments)
    }

    fun addFrequencyEnvelopeSegment(envelopeArguments: EnvelopeArguments) {
        frequencyEnvelopeSegments.add(envelopeArguments)
    }

}