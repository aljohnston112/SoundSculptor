#include <android/native_window.h>
#include <android/native_window_jni.h>

#include "AudioPlayer.h"
#include "EnvelopeRepository.h"
#include "JNIUtil.h"

constexpr int kSampleRate = 44100;
constexpr int kChannelCount = 1;

std::unique_ptr<AudioPlayer> audioPlayer;
std::shared_ptr<SineWaveGenerator> sineWaveGenerator;
std::unique_ptr<EnvelopeRepository> envelopeDataSource;


// TODO remove the envelopes
extern "C" JNIEXPORT void JNICALL
Java_io_fourth_1finger_sound_1sculptor_MainActivity_init(
        JNIEnv *env,
        jobject /* this */,
        jobjectArray functionEnumArrayFrequency,
        jobjectArray functionArgumentsFrequency,
        jobjectArray functionEnumArrayAmplitude,
        jobjectArray functionArgumentsAmplitude
) {
    std::shared_ptr<Envelope> frequencyEnvelope = make_envelope(
            env,
            functionEnumArrayFrequency,
            functionArgumentsFrequency,
            kSampleRate
    );

    std::shared_ptr<Envelope> amplitudeEnvelope = make_envelope(
            env,
            functionEnumArrayAmplitude,
            functionArgumentsAmplitude,
            kSampleRate
    );

    envelopeDataSource = std::make_unique<EnvelopeRepository>();
    envelopeDataSource->push_back_amplitude_envelope(amplitudeEnvelope);
    envelopeDataSource->push_back_frequency_envelope(frequencyEnvelope);

    sineWaveGenerator = std::make_shared<SineWaveGenerator>(
            kChannelCount,
            kSampleRate,
            frequencyEnvelope,
            amplitudeEnvelope
    );
}

extern "C" JNIEXPORT void JNICALL
Java_io_fourth_1finger_sound_1sculptor_PlayTouchButton_startPlaying(
        JNIEnv *env,
        jobject /* this */
) {
    audioPlayer = std::make_unique<AudioPlayer>(
            sineWaveGenerator,
            kChannelCount,
            kSampleRate
    );
}

extern "C" JNIEXPORT void JNICALL
Java_io_fourth_1finger_sound_1sculptor_PlayTouchButton_triggerRelease(
        JNIEnv *env,
        jobject /* this */
) {
    sineWaveGenerator->triggerRelease();
}

extern "C" JNIEXPORT void JNICALL
Java_io_fourth_1finger_sound_1sculptor_TitleFragment_stopPlaying(
        JNIEnv *env,
        jobject /* this */
) {
    audioPlayer->reset();
    audioPlayer.reset();
}

/**
 *
 * @param env
 * @param obj
 * @param functionArray An array of three enums that represent functions.
 * @param functionArguments A 2D array, with 3 rows,
 *                          containing the values needed to construct each function.
 */
extern "C" JNIEXPORT void JNICALL
Java_io_fourth_1finger_sound_1sculptor_EnvelopeFragment_setAmplitudeEnvelope(
        JNIEnv *env,
        jobject /* this */,
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

/**
 *
 * @param env
 * @param obj
 * @param functionArray An array of three enums that represent functions.
 * @param functionArguments A 2D array, with 3 rows,
 *                          containing the values needed to construct each function.
 */
extern "C" JNIEXPORT void JNICALL
Java_io_fourth_1finger_sound_1sculptor_EnvelopeFragment_setFrequencyEnvelope(
        JNIEnv *env,
        jobject /* this */,
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

extern "C" JNIEXPORT double JNICALL
Java_io_fourth_1finger_sound_1sculptor_FunctionView_getSeconds(
        JNIEnv *env,
        jobject clazz,
        jobject envelopeTypeEnum,
        int column
) {
    int enumValue = getEnumValue(env, envelopeTypeEnum);

    Envelope::EnvelopeType envelopeType;
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
Java_io_fourth_1finger_sound_1sculptor_FunctionView_getGraph(
        JNIEnv *env,
        jobject clazz,
        jobject buffer,
        jobject envelopeTypeEnum,
        int col,
        int width,
        int height
) {
    // Get the envelope
    int envelopeType = getEnumValue(env, envelopeTypeEnum);
    std::shared_ptr<Envelope> envelope;
    if (envelopeType == Envelope::EnvelopeType::AMPLITUDE) {
        envelope = envelopeDataSource->get_amplitude_envelope(col);
    } else {
        // envelopeType == Envelope::EnvelopeType::FREQUENCY
        envelope = envelopeDataSource->get_frequency_envelope(col);
    }

    auto minY = static_cast<float>(envelope->getMin());
    auto maxY = static_cast<float>(envelope->getMax());

    // Add room around constant
    if (minY == maxY) {
        minY += 1.0;
        maxY += 1.0;
    }

    static_assert(
            std::numeric_limits<float>::is_iec559 == true && sizeof(float) == 4,
            "IEEE754 needed to compile"
    );
    auto *bufferPtr = static_cast<char *>(env->GetDirectBufferAddress(buffer));
    if (bufferPtr != nullptr) {
        int increment = envelope->size() / width;
        for (int x = 0; x <= width; ++x) {
            auto y = static_cast<float>((*envelope)[x * increment]);
            std::memcpy(bufferPtr + (x * 4), &y, 4);
        }
        auto y = static_cast<float>((*envelope)[(*envelope).size() - 1]);
        std::memcpy(bufferPtr + ((width - 1) * 4), &y, 4);
    }
    jfloatArray min_max = env->NewFloatArray(2);
    jfloat mm[2]{minY, maxY};
    env->SetFloatArrayRegion(min_max, 0, 2, mm);
    return min_max;
}

extern "C" JNIEXPORT jint JNICALL
Java_io_fourth_1finger_sound_1sculptor_MainRecyclerViewAdapter_getNumEnvelopes(
        JNIEnv *env,
        jobject clazz
) {
    return envelopeDataSource->get_num_envelopes();
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_io_fourth_1finger_sound_1sculptor_FunctionView_isValidPosition(
        JNIEnv *env,
        jobject clazz,
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