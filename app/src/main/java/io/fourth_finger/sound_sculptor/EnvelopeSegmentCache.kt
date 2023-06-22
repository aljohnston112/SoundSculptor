package io.fourth_finger.sound_sculptor

import io.fourth_finger.sound_sculptor.data_class.EnvelopeArguments

/**
 * A cache for envelope segments' arguments.
 */
class EnvelopeSegmentCache {

    private val amplitudeEnvelopeSegments = mutableListOf<EnvelopeArguments>()
    private val frequencyEnvelopeSegments = mutableListOf<EnvelopeArguments>()

    /**
     * Append an envelope segment's arguments to the amplitude envelope arguments.
     * @param envelopeArguments The envelope arguments to be appended.
     */
    fun addAmplitudeEnvelopeSegmentArguments(envelopeArguments: EnvelopeArguments) {
        amplitudeEnvelopeSegments.add(envelopeArguments)
    }

    /**
     * Append an envelope segment's arguments to the frequency envelope arguments.
     * @param envelopeArguments The envelope arguments to be appended.
     */
    fun addFrequencyEnvelopeSegmentArguments(envelopeArguments: EnvelopeArguments) {
        frequencyEnvelopeSegments.add(envelopeArguments)
    }

    /**
     * @return The amplitude envelope arguments.
     */
    fun getAmplitudeEnvelopeArguments(): List<EnvelopeArguments> {
        return amplitudeEnvelopeSegments
    }

    /**
     * @return The frequency envelope arguments.
     */
    fun getFrequencyEnvelopeArguments(): List<EnvelopeArguments>  {
        return frequencyEnvelopeSegments
    }

}