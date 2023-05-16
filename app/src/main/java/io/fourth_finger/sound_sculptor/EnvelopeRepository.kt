package io.fourth_finger.sound_sculptor

import android.content.Context

class EnvelopeRepository(private val envelopeDataSource: EnvelopeDataSource) {

    private val envelopeCache: MutableMap<String, EnvelopeData> = mutableMapOf()

    suspend fun getEnvelopeData(context: Context, name: String): EnvelopeData? {
        val envelopeData = envelopeCache[name] ?: envelopeDataSource.loadEnvelope(context, name)
        envelopeData?.let {
            envelopeCache[name] = it
        }
        return envelopeData
    }

    suspend fun saveEnvelopeData(context: Context, envelopeData: EnvelopeData, name: String) {
        envelopeDataSource.saveEnvelope(context, envelopeData, name)
        envelopeCache[name] = envelopeData
    }

    suspend fun deleteEnvelopeData(context: Context, name: String) {
        envelopeDataSource.deleteEnvelope(context, name)
        envelopeCache.remove(name)
    }

}