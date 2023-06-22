#include <android/native_window.h>
#include <android/native_window_jni.h>

#include "AudioPlayer.h"
#include "ASREnvelope.h"
#include "EnvelopeSegmentCache.h"
#include "JNIUtil.h"
#include "EnvelopeSegment.h"

constexpr int kSampleRate = 44100;
constexpr int kChannelCount = 1;

std::unique_ptr<AudioPlayer> audioPlayer;

std::shared_ptr<EnvelopeSegmentCache> amplitudeEnvelopeSegmentCache;
std::shared_ptr<EnvelopeSegmentCache> frequencyEnvelopeSegmentCache;
std::shared_ptr<AudioGenerator> audioGenerator;

/**
 * Clears the envelope caches and creates an audio generator
 */
extern "C" JNIEXPORT void JNICALL
Java_io_fourth_1finger_sound_1sculptor_JNIFunctionsKt_init(
        JNIEnv *env,
        jclass clazz
) {
    amplitudeEnvelopeSegmentCache = std::make_unique<EnvelopeSegmentCache>();
    frequencyEnvelopeSegmentCache = std::make_unique<EnvelopeSegmentCache>();
    audioGenerator = std::make_shared<SineWaveGenerator>(
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
        jclass clazz
) {
    audioPlayer = std::make_unique<AudioPlayer>(
            audioGenerator,
            kChannelCount,
            kSampleRate
    );
    std::shared_ptr<Envelope> amplitudeEnvelope = amplitudeEnvelopeSegmentCache->get_envelope_segments_as_envelope();
    std::shared_ptr<Envelope> frequencyEnvelope = frequencyEnvelopeSegmentCache->get_envelope_segments_as_envelope();
    audioGenerator->setAmplitudeEnvelope(amplitudeEnvelope);
    audioGenerator->setFrequencyEnvelope(frequencyEnvelope);
}

extern "C" JNIEXPORT void JNICALL
/**
 * Triggers the release portion of the envelope_segments.
 */
Java_io_fourth_1finger_sound_1sculptor_JNIFunctionsKt_triggerRelease(
        JNIEnv *env,
        jclass clazz
) {
    // audioGenerator->triggerRelease();
}

extern "C" JNIEXPORT void JNICALL
/**
 * Stops and releases the audio player
 */
Java_io_fourth_1finger_sound_1sculptor_JNIFunctionsKt_stopPlaying(
        JNIEnv *env,
        jclass clazz
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
    if (enumValue == EnvelopeType::AMPLITUDE) {
        size = amplitudeEnvelopeSegmentCache->size_of_combined_envelopes(column);
    } else {
        // enumValue == ASREnvelope::EnvelopeType::FREQUENCY
        size = frequencyEnvelopeSegmentCache->size_of_combined_envelopes(column);
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
Java_io_fourth_1finger_sound_1sculptor_JNIFunctionsKt_fillBufferWithGraph(
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
    if (envelopeType == EnvelopeType::AMPLITUDE) {
        envelope = amplitudeEnvelopeSegmentCache->get_envelope_segment(column);
    } else {
        // envelopeType == EnvelopeType::FREQUENCY
        envelope = frequencyEnvelopeSegmentCache->get_envelope_segment(column);
    }

    // Fill the buffer with envelope data
    auto *bufferPtr = static_cast<char *>(
            env->GetDirectBufferAddress(buffer)
    );
    if (bufferPtr != nullptr) {
        uint32_t increment = envelope->size() / width;
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

extern "C" JNIEXPORT jfloatArray JNICALL
Java_io_fourth_1finger_sound_1sculptor_JNIFunctionsKt_getMinMax(
        JNIEnv *env,
        jclass clazz,
        jobject envelopeTypeEnum,
        jint column
) {
    // Get the envelope
    int envelopeType = getEnumValue(env, envelopeTypeEnum);
    std::shared_ptr<Envelope> envelope;
    if (envelopeType == EnvelopeType::AMPLITUDE) {
        envelope = amplitudeEnvelopeSegmentCache->get_envelope_segment(column);
    } else {
        // envelopeType == EnvelopeType::FREQUENCY
        envelope = frequencyEnvelopeSegmentCache->get_envelope_segment(column);
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
 * Gets the total number of envelope_segments.
 * This includes every type of envelope.
 *
 * @return The total number of envelope_segments.
 */
Java_io_fourth_1finger_sound_1sculptor_JNIFunctionsKt_getNumEnvelopeSegments(
        JNIEnv *env,
        jclass clazz
) {
    return static_cast<jint>(
            amplitudeEnvelopeSegmentCache->get_num_envelope_segments() +
            frequencyEnvelopeSegmentCache->get_num_envelope_segments()
    );
}

extern "C" JNIEXPORT jint JNICALL
Java_io_fourth_1finger_sound_1sculptor_JNIFunctionsKt_getNumAmplitudeEnvelopeSegments(
        JNIEnv *env,
        jclass clazz
) {
    return static_cast<jint>(amplitudeEnvelopeSegmentCache->get_num_envelope_segments());
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
    if (envelopeType == EnvelopeType::AMPLITUDE) {
        isValid = column < amplitudeEnvelopeSegmentCache->get_num_envelope_segments();
    } else {
        // envelopeType == ASREnvelope::EnvelopeType::FREQUENCY
        isValid = column < frequencyEnvelopeSegmentCache->get_num_envelope_segments();
    }

    return isValid;
}


/**
 * Creates an envelope with the given parameters.
 *
 * @param functionEnumArray The types of functions to use for the
 *                          attack, sustain and release portions of the
 *                          envelope_segments.
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
    std::shared_ptr<Envelope> envelope;
    if (functionTypes.size() == 3) {
        envelope = make_asr_envelope(functionTypes, functionArguments, sampleRate);
    } else if (functionTypes.size() == 1) {
        envelope = make_envelope_segment(
                functionTypes[0],
                functionArguments[0],
                sampleRate
        );
    }
    return envelope;
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
Java_io_fourth_1finger_sound_1sculptor_JNIFunctionsKt_addFrequencyEnvelopeSegmentArguments(
        JNIEnv *env,
        jclass clazz,
        jobjectArray functionEnumArray,
        jobjectArray functionArguments
) {
    std::shared_ptr<Envelope> frequencyEnvelope = make_envelope(
            env,
            functionEnumArray,
            functionArguments,
            kSampleRate
    );
    frequencyEnvelopeSegmentCache->push_back_envelope_segment(frequencyEnvelope);
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
Java_io_fourth_1finger_sound_1sculptor_JNIFunctionsKt_addAmplitudeEnvelopeSegmentArguments(
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
    amplitudeEnvelopeSegmentCache->push_back_envelope_segment(amplitudeEnvelope);
}

extern "C"
JNIEXPORT jobject JNICALL
Java_io_fourth_1finger_sound_1sculptor_JNIFunctionsKt_getEnvelopeSegmentType(
        JNIEnv *env,
        jclass clazz,
        jint index
) {
    if (index >=
        (amplitudeEnvelopeSegmentCache->get_num_envelope_segments() +
         frequencyEnvelopeSegmentCache->get_num_envelope_segments()) + 2) {
        throw std::invalid_argument("Index out of bounds");
    }
    jclass envelopeTypeClass = env->FindClass(
            "io/fourth_finger/sound_sculptor/Envelope$EnvelopeType");
    jmethodID valuesMethod = env->GetStaticMethodID(
            envelopeTypeClass,
            "values",
            "()[Lio/fourth_finger/sound_sculptor/Envelope$EnvelopeType;"
    );
    auto envelopeTypeArray = (jobjectArray) env->CallStaticObjectMethod(envelopeTypeClass,
                                                                        valuesMethod);
    jobject envelopeTypeJNI;
    if (index <= amplitudeEnvelopeSegmentCache->get_num_envelope_segments()) {
        envelopeTypeJNI = env->GetObjectArrayElement(
                envelopeTypeArray,
                static_cast<int>(EnvelopeType::AMPLITUDE)
        );
    } else {
        envelopeTypeJNI = env->GetObjectArrayElement(
                envelopeTypeArray,
                static_cast<int>(EnvelopeType::FREQUENCY)
        );
    }
    return envelopeTypeJNI;
}

extern "C"
JNIEXPORT jint JNICALL
Java_io_fourth_1finger_sound_1sculptor_JNIFunctionsKt_getColumnNumber(
        JNIEnv *env,
        jclass clazz,
        jint index
) {
    if (index >=
        (amplitudeEnvelopeSegmentCache->get_num_envelope_segments() +
         frequencyEnvelopeSegmentCache->get_num_envelope_segments()) + 2) {
        throw std::invalid_argument("Index out of bounds");
    }

    jint column;
    if (index <= amplitudeEnvelopeSegmentCache->get_num_envelope_segments()) {
        column = index;
    } else {
        column = index -
                 static_cast<jint>(amplitudeEnvelopeSegmentCache->get_num_envelope_segments() + 1);
    }
    return column;
}