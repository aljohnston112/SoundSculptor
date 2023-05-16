package io.fourth_finger.sound_sculptor

import android.content.Context
import io.fourth_finger.sound_sculptor.FileUtil.Companion.delete
import io.fourth_finger.sound_sculptor.FileUtil.Companion.load
import io.fourth_finger.sound_sculptor.FileUtil.Companion.save
import java.io.Serializable

data class EnvelopeData(
    val attackFunction: String,
    val attackStart: Double,
    val attackEnd: Double,
    val attackTime: Double,
    val sustainFunction: String,
    val sustainEnd: Double,
    val sustainTime: Double,
    val releaseFunction: String,
    val releaseEnd: Double,
    val releaseTime: Double
) : Serializable

class EnvelopeDataSource {

    suspend fun saveEnvelope(
        context: Context,
        envelopeData: EnvelopeData,
        name: String
    ){
        save(envelopeData, context, name, FILE_VERIFICATION_NUMBER)
    }

    suspend fun loadEnvelope(
        context: Context,
        name: String
    ): EnvelopeData? {
        return load(context, name, FILE_VERIFICATION_NUMBER)
    }

    suspend fun deleteEnvelope(
        context: Context,
        name: String
    ){
        delete(context, name)
    }

    companion object {
        const val FILE_VERIFICATION_NUMBER = 2495762349506;
    }

}