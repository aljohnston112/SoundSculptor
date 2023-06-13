package io.fourth_finger.sound_sculptor

class EnvelopeSegmentsUseCase {

    private val envelopeSegmentCache = EnvelopeSegmentCache()

    fun addAmplitudeEnvelopeSegment(envelopeArguments: EnvelopeArguments) {
        envelopeSegmentCache.addAmplitudeEnvelopeSegment(
            envelopeArguments
        )
    }

    fun addFrequencyEnvelopeSegment(envelopeArguments: EnvelopeArguments) {
        envelopeSegmentCache.addFrequencyEnvelopeSegment(
            envelopeArguments
        )
    }

}