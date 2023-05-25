package io.fourth_finger.sound_sculptor

import android.content.Context
import io.fourth_finger.sound_sculptor.FileUtil.Companion.loadList
import io.fourth_finger.sound_sculptor.FileUtil.Companion.saveList
import java.io.Serializable


/**
 * Parameters for creating an ASREnvelope.
 */
data class EnvelopeData(
    val attackFunction: Envelope.FunctionType,
    val attackStart: Double,
    val attackEnd: Double,
    val attackTime: Double,
    val sustainFunction: Envelope.FunctionType,
    val sustainEnd: Double,
    val sustainTime: Double,
    val releaseFunction: Envelope.FunctionType,
    val releaseEnd: Double,
    val releaseTime: Double
) : Serializable

/**
 * The data source for envelopes.
 * The envelopes are saved to a file
 */
class EnvelopeDataSource {

    /**
     * Saves envelope data to a file.
     *
     * @param context
     * @param envelopeData The list of envelope data to save.
     * @param name The name of the file to save the envelope data to.
     */
    suspend fun saveEnvelopes(
        context: Context,
        envelopeData: List<EnvelopeData>,
        name: String
    ){
        saveList(envelopeData, context, name, FILE_VERIFICATION_NUMBER)
    }

    /**
     * Loads envelope data from a file.
     *
     * @param context
     * @param name The name of the file to load the envelope data from.
     * @return The envelope data loaded from the file with the given name.
     */
    suspend fun loadEnvelopes(
        context: Context,
        name: String
    ): List<EnvelopeData>? {
        return loadList(context, name, FILE_VERIFICATION_NUMBER)
    }

    companion object {
        /**
         * The number used to verify files when loading data.
         */
        private const val FILE_VERIFICATION_NUMBER = 2495762349506
    }

}