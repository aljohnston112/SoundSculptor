package io.fourth_finger.sound_sculptor.data_class

import java.io.Serializable

/**
 * Contains enums for specifying envelope details.
 */
class Envelope(
    val name: String,
    val amplitudeEnvelopeArguments: List<EnvelopeArguments>,
    val frequencyEnvelopeArguments: List<EnvelopeArguments>
) : Serializable {

    /**
     * Types of values an envelope can represent.
     */
    enum class EnvelopeType(val value: Int) {
        AMPLITUDE(0),
        FREQUENCY(1)
    }

}