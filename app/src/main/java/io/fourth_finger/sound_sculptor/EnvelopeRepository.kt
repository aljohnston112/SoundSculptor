package io.fourth_finger.sound_sculptor

import android.content.Context

/**
 * Saves and loads envelope data.
 *
 * @param envelopeDataSource The data source that provides the
 *                           persistent storage for the envelopes.
 */
class EnvelopeRepository(private val envelopeDataSource: EnvelopeDataSource) {

    private val envelopeCache: MutableMap<String, List<EnvelopeData>> =
        mutableMapOf()

    /**
     * Gets a list of named envelopes.
     *
     * @param context
     * @param name The name of the envelope data to get.
     * @return The list of envelopes with the given name.
     */
    suspend fun getEnvelopeData(
        context: Context,
        name: String
    ): List<EnvelopeData>? {
        var envelopeData = envelopeCache[name]
        if(envelopeData == null){
            envelopeData = envelopeDataSource.loadEnvelopes(context, name)
        }
        return envelopeData
    }

    /**
     * Saves a named list of envelopes.
     *
     * @param context
     * @param envelopeData The envelope data to save.
     * @param name The name of the envelope data.
     */
    suspend fun saveEnvelopeData(
        context: Context,
        envelopeData: List<EnvelopeData>,
        name: String
    ) {
        envelopeDataSource.saveEnvelopes(context, envelopeData, name)
        envelopeCache[name] = envelopeData
    }

}