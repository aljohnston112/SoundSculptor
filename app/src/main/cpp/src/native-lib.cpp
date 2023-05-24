#include <android/native_window.h>
#include <android/native_window_jni.h>

#include "AudioPlayer.h"
#include "Envelope.h"
#include "EnvelopeRepository.h"
#include "JNIUtil.h"

constexpr int kSampleRate = 44100;
constexpr int kChannelCount = 1;

std::unique_ptr<AudioPlayer> audioPlayer;
std::shared_ptr<SineWaveGenerator> sineWaveGenerator;
std::unique_ptr<EnvelopeRepository> envelopeDataSource;

extern "C" JNIEXPORT void JNICALL
/**
 * Initializes the sound generator.
 */
Java_io_fourth_1finger_sound_1sculptor_JNIFunctionsKt_init(
        JNIEnv *env,
        jclass clazz
) {
    envelopeDataSource = std::make_unique<EnvelopeRepository>();

    sineWaveGenerator = std::make_shared<SineWaveGenerator>(
            kChannelCount,
            kSampleRate
    );
}

extern "C" JNIEXPORT void JNICALL
/**
 * Starts playing sound with the current settings.
 */
Java_io_fourth_1finger_sound_1sculptor_JNIFunctionsKt_startPlaying(
        JNIEnv *env,
        jclass  clazz
) {
    audioPlayer = std::make_unique<AudioPlayer>(
            sineWaveGenerator,
            kChannelCount,
            kSampleRate
    );
}

extern "C" JNIEXPORT void JNICALL
/**
 * Triggers the release portion of the envelopes.
 */
Java_io_fourth_1finger_sound_1sculptor_JNIFunctionsKt_triggerRelease(
        JNIEnv *env,
        jclass  clazz
) {
    sineWaveGenerator->triggerRelease();
}

extern "C" JNIEXPORT void JNICALL
/**
 * Stops and releases the audio player
 */
Java_io_fourth_1finger_sound_1sculptor_JNIFunctionsKt_stopPlaying(
        JNIEnv *env,
        jclass  clazz
) {
    audioPlayer->reset();
    audioPlayer.reset();
}

extern "C" JNIEXPORT double JNICALL
/**
 * Get the number of seconds an envelope lasts.
 *
 * @param envelopeTypeEnum The type of envelope.
 * @param column The column of the envelope.
 * @return The number of seconds the envelope of the given type
 *         at the given column lasts.
 */
Java_io_fourth_1finger_sound_1sculptor_JNIFunctionsKt_getSeconds(
        JNIEnv *env,
        jclass clazz,
        jobject envelopeTypeEnum,
        jint column
) {
    int enumValue = getEnumValue(env, envelopeTypeEnum);
    int64_t size;
    if (enumValue == Envelope::EnvelopeType::AMPLITUDE) {
        size = envelopeDataSource->amplitude_envelope_size(column);
    } else {
        // enumValue == Envelope::EnvelopeType::FREQUENCY
        size = envelopeDataSource->frequency_envelope_size(column);
    }
    return (static_cast<double>(size) / kSampleRate);

}

extern "C" JNIEXPORT jfloatArray JNICALL
/**
 * Fills a float buffer with the graph of an envelope.
 *
 * @param buffer The float buffer to fill with the graph.
 * @param envelopeTypeEnum The type of envelope to graph.
 * @param column The column of the envelope to graph.
 * @param width The width of the buffer.
 * @return An array with the min value of the graph at the first index and
 *         the max value of the graph at the second index.
 */
Java_io_fourth_1finger_sound_1sculptor_JNIFunctionsKt_getGraph(
        JNIEnv *env,
        jclass clazz,
        jobject buffer,
        jobject envelopeTypeEnum,
        jint column,
        jint width
) {

    static_assert(
            std::numeric_limits<float>::is_iec559 == true &&
            sizeof(float) == 4,
            "IEEE754 needed to compile"
    );

    // Get the envelope
    int envelopeType = getEnumValue(env, envelopeTypeEnum);
    std::shared_ptr<Envelope> envelope;
    if (envelopeType == Envelope::EnvelopeType::AMPLITUDE) {
        envelope = envelopeDataSource->get_amplitude_envelope(column);
    } else {
        // envelopeType == Envelope::EnvelopeType::FREQUENCY
        envelope = envelopeDataSource->get_frequency_envelope(column);
    }

    // Fill the buffer with envelope data
    auto *bufferPtr = static_cast<char *>(
            env->GetDirectBufferAddress(buffer)
    );
    if (bufferPtr != nullptr) {
        int32_t increment = envelope->size() / width;
        uint8_t numBytes = 4;
        for (int32_t x = 0; x < width - 1; ++x) {
            auto y = static_cast<float>((*envelope)[x * increment]);
            std::memcpy(
                    bufferPtr + (x * numBytes),
                    &y,
                    numBytes
            );
        }
        // Last element must be the last
        auto y = static_cast<float>((*envelope)[(*envelope).size() - 1]);
        std::memcpy(
                bufferPtr + ((width - 1) * numBytes),
                &y,
                numBytes
        );
    }

    // Return the min and max
    jfloatArray min_max = env->NewFloatArray(2);
    auto minY = static_cast<float>(envelope->getMin());
    auto maxY = static_cast<float>(envelope->getMax());
    jfloat mm[2]{minY, maxY};
    env->SetFloatArrayRegion(min_max, 0, 2, mm);
    return min_max;
}

extern "C" JNIEXPORT jint JNICALL
/**
 * Gets the total number of envelopes.
 * This includes every type of envelope.
 *
 * @return The total number of envelopes.
 */
Java_io_fourth_1finger_sound_1sculptor_JNIFunctionsKt_getNumEnvelopes(
        JNIEnv *env,
        jclass clazz
) {
    return static_cast<jint>(envelopeDataSource->get_num_envelopes());
}

extern "C" JNIEXPORT jboolean JNICALL
/**
 * Checks if a given column is a valid index.
 *
 * @param envelopeTypeEnum The type of envelope.
 * @param column The column to check for.
 * @return true if the column exists, else false.
 */
Java_io_fourth_1finger_sound_1sculptor_JNIFunctionsKt_isValidPosition(
        JNIEnv *env,
        jclass clazz,
        jobject envelopeTypeEnum,
        jint column
) {
    int envelopeType = getEnumValue(env, envelopeTypeEnum);
    bool isValid;
    if (envelopeType == Envelope::EnvelopeType::AMPLITUDE) {
        isValid = column < envelopeDataSource->get_num_amplitude_envelopes();
    } else {
        // envelopeType == Envelope::EnvelopeType::FREQUENCY
        isValid = column < envelopeDataSource->get_num_frequency_envelopes();
    }

    return isValid;
}


/**
 * Creates an envelope with the given parameters.
 *
 * @param functionEnumArray The types of functions to use for the
 *                          attack, sustain and release portions of the
 *                          envelopes.
 * @param functionArgumentsArray The arguments used to create the
 *                               functions for each portion of the envelope.
 * @param sampleRate The sample rate.
 * @return A shared pointer to the created envelope.
 */
std::shared_ptr<Envelope> make_envelope(
        JNIEnv *env,
        jobjectArray functionEnumArray,
        jobjectArray functionArgumentsArray,
        int64_t sampleRate
) {
    std::vector<FunctionType> functionTypes = convertToFunctionTypeVector(
            env,
            functionEnumArray
    );
    std::vector<std::vector<double>> functionArguments = convertTo2DVector(
            env,
            functionArgumentsArray
    );
    return make_envelope(functionTypes, functionArguments, sampleRate);
}

extern "C" JNIEXPORT void JNICALL
/**
 * Creates a frequency envelope with the given parameters and
 * sets it to the current sound generator.
 *
 * @param functionArray An array of three enums that specify the functions
 *                      to create the attack, sustain and release portions
 *                      of the envelope with.
 * @param functionArguments A 2D array, with 3 rows,
 *                          where each row contains the values needed to
 *                          construct the corresponding function in the
 *                          functionArray.
 */
Java_io_fourth_1finger_sound_1sculptor_JNIFunctionsKt_setFrequencyEnvelope(
        JNIEnv *env,
        jclass  clazz,
        jobjectArray functionEnumArray,
        jobjectArray functionArguments
) {
    std::shared_ptr<Envelope> frequencyEnvelope = make_envelope(
            env,
            functionEnumArray,
            functionArguments,
            kSampleRate
    );
    sineWaveGenerator->setFrequencyEnvelope(frequencyEnvelope);
}

extern "C" JNIEXPORT void JNICALL
/**
 * Creates an amplitude envelope with the given parameters and
 * sets it to the current sound generator.
 *
 * @param functionArray An array of three enums that specify the functions
 *                      to create the attack, sustain and release portions
 *                      of the envelope with.
 * @param functionArguments A 2D array, with 3 rows,
 *                          where each row contains the values needed to
 *                          construct the corresponding function in the
 *                          functionArray.
 */
Java_io_fourth_1finger_sound_1sculptor_JNIFunctionsKt_setAmplitudeEnvelope(
        JNIEnv *env,
        jclass clazz,
        jobjectArray functionEnumArray,
        jobjectArray functionArguments
) {

    std::shared_ptr<Envelope> amplitudeEnvelope = make_envelope(
            env,
            functionEnumArray,
            functionArguments,
            kSampleRate
    );
    sineWaveGenerator->setAmplitudeEnvelope(amplitudeEnvelope);
}