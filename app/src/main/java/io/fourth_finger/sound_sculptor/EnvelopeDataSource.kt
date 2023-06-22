package io.fourth_finger.sound_sculptor

import android.content.Context
import io.fourth_finger.sound_sculptor.util.FileUtil.Companion.load
import io.fourth_finger.sound_sculptor.util.FileUtil.Companion.loadList
import io.fourth_finger.sound_sculptor.util.FileUtil.Companion.save
import io.fourth_finger.sound_sculptor.util.FileUtil.Companion.saveList
import io.fourth_finger.sound_sculptor.data_class.Envelope

/**
 * The data source for envelopes.
 * The envelopes are saved to a file with the same name as the envelope name.
 */
class EnvelopeDataSource {

    private val fileNames = mutableListOf<String>()

    /**
     * Saves an envelope to a file.
     *
     * @param envelope The envelope to save.
     * @param name The name of the file to save the envelope to.
     */
    suspend fun saveEnvelope(
        context: Context,
        envelope: Envelope,
        name: String
    ){
        fileNames.add(name)
        saveList(fileNames, context, ENVELOPE_FILE_NAMES_FILE, FILE_VERIFICATION_NUMBER)
        save(envelope, context, name, FILE_VERIFICATION_NUMBER)
    }

    /**
     * Loads an envelope from a file.
     *
     * @param context
     * @param name The name of the file to load the envelope from.
     * @return The envelope loaded from the file with the given name.
     */
    suspend fun loadEnvelope(
        context: Context,
        name: String
    ): Envelope? {
        return load(context, name, FILE_VERIFICATION_NUMBER)
    }

    /**
     * Loads the names of all the envelope files.
     */
    suspend fun loadEnvelopeNames(context: Context): List<String> {
        val fileNames = loadList<String>(context, ENVELOPE_FILE_NAMES_FILE, FILE_VERIFICATION_NUMBER)
        this.fileNames.clear()
        if (fileNames != null) {
            this.fileNames.addAll(fileNames)
        }
        return this.fileNames
    }

    companion object {

        /**
         * The number used to verify files when loading data.
         */
        private const val FILE_VERIFICATION_NUMBER = 2495762349506

        /**
         * The file containing all the files saved by this app
         */
        private const val ENVELOPE_FILE_NAMES_FILE = "FILE_NAMES.hopefully_nobody_uses_a_file_with_this_name"

    }

}