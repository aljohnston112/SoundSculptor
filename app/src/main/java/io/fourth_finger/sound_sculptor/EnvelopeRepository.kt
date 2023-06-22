package io.fourth_finger.sound_sculptor

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.fourth_finger.sound_sculptor.data_class.Envelope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Saves and loads envelopes.
 *
 * @param envelopeDataSource The data source that provides the
 *                           persistent storage for the envelope_segments.
 */
class EnvelopeRepository(
    private val envelopeDataSource: EnvelopeDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    private val envelopeCache = mutableMapOf<String, Envelope>()

    private val _envelopeNames = MutableLiveData<Set<String>>()
    val envelopeNames: LiveData<Set<String>> = _envelopeNames

    /**
     * Loads a named envelope.
     *
     * @param name The name of the envelope to get.
     * @return The envelope with the given name or null if the file was not found.
     */
    suspend fun loadEnvelope(
        context: Context,
        name: String
    ): Envelope? {
        var envelope = envelopeCache[name]
        if (envelope == null) {
            envelope = withContext(ioDispatcher) { envelopeDataSource.loadEnvelope(context, name) }
            if (envelope != null) {
                envelopeCache[name] = envelope
            }
        }
        return envelope
    }

    /**
     * Saves a named envelope.
     *
     * @param envelope The envelope to save.
     */
    suspend fun saveEnvelope(
        context: Context,
        envelope: Envelope,
    ) {
        withContext(ioDispatcher) {
            envelopeDataSource.saveEnvelope(
                context,
                envelope,
                envelope.name
            )
        }
        envelopeCache[envelope.name] = envelope
        val oldNames = _envelopeNames.value ?: mutableListOf()
        val newNames = oldNames.toMutableSet()
        newNames.add(envelope.name)
        _envelopeNames.postValue(newNames)
    }

    /**
     * Loads the names of the current envelopes.
     */
    suspend fun loadEnvelopeNames(context: Context) {
        val newNames = envelopeDataSource.loadEnvelopeNames(context)
        _envelopeNames.postValue(newNames.toSet())
    }

}