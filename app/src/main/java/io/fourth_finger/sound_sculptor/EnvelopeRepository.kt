package io.fourth_finger.sound_sculptor

import android.content.Context

/**
 * Saves and loads envelope data.
 *
 * @param envelopeDataSource The data source that provides the
 *                           persistent storage for the envelope_segments.
 */
class EnvelopeRepository(private val envelopeDataSource: EnvelopeDataSource) {

    private val envelopeCache = mutableMapOf<String, Envelope>()

    suspend fun getAmplitudeEnvelope(context: Context, name: String): Envelope? {
        return getEnvelope(context, "AMPLITUDE_$name")
    }

    suspend fun getFrequencyEnvelope(context: Context, name: String): Envelope? {
        return getEnvelope(context, "FREQUENCY_$name")
    }

    /**
     * Gets a named envelope.
     *
     * @param context
     * @param name The name of the envelope to get.
     * @return The envelope_segments with the given name.
     */
    private suspend fun getEnvelope(
        context: Context,
        name: String
    ): Envelope? {
        var envelope = envelopeCache[name]
        if (envelope == null) {
            envelope = envelopeDataSource.loadEnvelope(context, name)
            if(envelope != null) {
                envelopeCache[name] = envelope
            }
        }
        return envelope
    }

    suspend fun saveAmplitudeEnvelope(
        context: Context,
        envelope: Envelope,
        name: String
    ) {
        saveEnvelope(context, envelope, "AMPLITUDE_$name")
    }

    suspend fun saveFrequencyEnvelope(
        context: Context,
        envelope: Envelope,
        name: String
    ) {
        saveEnvelope(context, envelope, "FREQUENCY_$name")
    }

    /**
     * Saves a named envelope.
     *
     * @param context
     * @param envelope The envelope to save.
     * @param name The name of the envelope.
     */
    private suspend fun saveEnvelope(
        context: Context,
        envelope: Envelope,
        name: String
    ) {
        envelopeDataSource.saveEnvelope(context, envelope, name)
        envelopeCache[name] = envelope
    }

}