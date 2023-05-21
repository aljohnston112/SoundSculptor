#include <android/native_window.h>
#include <android/native_window_jni.h>

#include "AudioPlayer.h"

constexpr int kSampleRate = 44100;
constexpr int kChannelCount = 1;

std::unique_ptr<AudioPlayer> audioPlayer;
std::shared_ptr<SineWaveGenerator> sineWaveGenerator;
std::unique_ptr<std::vector<std::vector<std::shared_ptr<Envelope>>>> envelopes;

std::vector<double> convertToArray(JNIEnv *env, jdoubleArray jDoubleArrayArgs) {
    std::vector<double> doubles(env->GetArrayLength(jDoubleArrayArgs));
    jdouble *elements = env->GetDoubleArrayElements(jDoubleArrayArgs, nullptr);
    for (int i = 0; i < env->GetArrayLength(jDoubleArrayArgs); ++i) {
        doubles[i] = (elements[i]);
    }
    env->ReleaseDoubleArrayElements(jDoubleArrayArgs, elements, 0);
    return doubles;
}

std::shared_ptr<std::vector<double>> generateSegment(int function, std::vector<double> args) {
    std::shared_ptr<std::vector<double>> segment;
    double time = args.at(2);
    int numSamples = static_cast<int>(std::round(static_cast<double>(kSampleRate) * time));
    switch (function) {
        case 0:
            segment = VectorGenerator::generateLinearSegment(
                    args.at(0),
                    args.at(1),
                    numSamples
            );
            break;
    }
    return segment;
}

std::shared_ptr<Envelope> make_envelope(
        JNIEnv *env,
        jintArray functionEnumArray,
        jobjectArray functionArguments
) {
    // Get function enums
    jint *enumElements = env->GetIntArrayElements(functionEnumArray, nullptr);
    int firstFunction = enumElements[0];
    int secondFunction = enumElements[1];
    int thirdFunction = enumElements[2];
    env->ReleaseIntArrayElements(
            functionEnumArray,
            enumElements,
            0
    );

    // Make attack segment
    auto jAttackFunctionArgs = static_cast<jdoubleArray>(
            env->GetObjectArrayElement(functionArguments, 0)
    );
    std::vector<double> attackFunctionArgs = convertToArray(
            env,
            jAttackFunctionArgs
    );
    std::shared_ptr<std::vector<double>> attack = generateSegment(
            firstFunction,
            attackFunctionArgs
    );

    // Make sustain segment
    auto jSustainFunctionArgs = static_cast<jdoubleArray>(
            env->GetObjectArrayElement(functionArguments, 1)
    );
    std::vector<double> sustainFunctionArgs = convertToArray(
            env,
            jSustainFunctionArgs
    );
    std::shared_ptr<std::vector<double>> sustain = generateSegment(
            secondFunction,
            sustainFunctionArgs
    );

    // Make release segment
    auto jReleaseFunctionArgs = static_cast<jdoubleArray>(
            env->GetObjectArrayElement(functionArguments, 2)
    );
    std::vector<double> releaseFunctionArgs = convertToArray(
            env,
            jReleaseFunctionArgs
    );
    releaseFunctionArgs.push_back(releaseFunctionArgs[1]);
    releaseFunctionArgs[1] = releaseFunctionArgs[0];
    releaseFunctionArgs[0] = sustainFunctionArgs[1];
    std::shared_ptr<std::vector<double>> release = generateSegment(
            thirdFunction,
            releaseFunctionArgs
    );

    return std::make_shared<Envelope>(
            attack,
            sustain,
            release,
            true
    );
}

extern "C" JNIEXPORT void JNICALL
Java_io_fourth_1finger_sound_1sculptor_MainActivity_init(
        JNIEnv *env,
        jobject /* this */,
        jintArray functionEnumArrayFrequency,
        jobjectArray functionArgumentsFrequency,
        jintArray functionEnumArrayAmplitude,
        jobjectArray functionArgumentsAmplitude
) {
    std::shared_ptr<Envelope> frequencyEnvelope = make_envelope(
            env,
            functionEnumArrayFrequency,
            functionArgumentsFrequency
    );

    std::shared_ptr<Envelope> amplitudeEnvelope = make_envelope(
            env,
            functionEnumArrayAmplitude,
            functionArgumentsAmplitude
    );

    envelopes = std::make_unique<std::vector<std::vector<std::shared_ptr<Envelope>>>>();

    // Amplitude envelopes
    (*envelopes).emplace_back(std::vector<std::shared_ptr<Envelope>>());

    // Frequency envelopes
    (*envelopes).emplace_back(std::vector<std::shared_ptr<Envelope>>());

    (*envelopes)[0].push_back(amplitudeEnvelope);
    (*envelopes)[1].push_back(frequencyEnvelope);

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
        jintArray functionEnumArray,
        jobjectArray functionArguments
) {
    std::shared_ptr<Envelope> amplitudeEnvelope = make_envelope(
            env,
            functionEnumArray,
            functionArguments
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
        jintArray functionEnumArray,
        jobjectArray functionArguments
) {
    std::shared_ptr<Envelope> frequencyEnvelope = make_envelope(
            env,
            functionEnumArray,
            functionArguments
    );
    sineWaveGenerator->setFrequencyEnvelope(frequencyEnvelope);
}

extern "C" JNIEXPORT double JNICALL
Java_io_fourth_1finger_sound_1sculptor_FunctionView_getXToYRatio(
        JNIEnv *env,
        jobject clazz,
        int row,
        int col
) {
    double size = (*envelopes)[row][col]->getSize();
    return (size / kSampleRate);
}

extern "C" JNIEXPORT jfloatArray JNICALL
Java_io_fourth_1finger_sound_1sculptor_FunctionView_getGraph(
        JNIEnv *env,
        jobject clazz,
        jobject buffer,
        int row,
        int col,
        int width,
        int height
) {
    std::shared_ptr<Envelope> envelope = (*envelopes)[row][col];
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
        int increment = envelope->getSize() / width;
        for (int x = 0; x <= width; ++x) {
            auto y = static_cast<float>((*envelope)[x * increment]);
            std::memcpy(bufferPtr + (x * 4), &y, 4);
        }
        auto y = static_cast<float>((*envelope)[(*envelope).getSize() - 1]);
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
    int size = 0;
    for (auto &i: *envelopes) {
        size += static_cast<int>(i.size());
    }
    return size;
}