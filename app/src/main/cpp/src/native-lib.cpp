#include <android/native_window.h>
#include <android/native_window_jni.h>

#include "AudioPlayer.h"

constexpr int kSampleRate = 44100;
constexpr int kChannelCount = 1;

std::unique_ptr<AudioPlayer> audioPlayer;
std::shared_ptr<SineWaveGenerator> sineWaveGenerator;

std::unique_ptr<std::vector<std::vector<std::shared_ptr<Envelope>>>> envelopes;

std::vector<double> convertToArray(JNIEnv *env, jdoubleArray jDoubleArray) {
    std::vector<double> doubles(env->GetArrayLength(jDoubleArray));
    jdouble *elements = env->GetDoubleArrayElements(jDoubleArray, nullptr);
    for (int i = 0; i < env->GetArrayLength(jDoubleArray); ++i) {
        doubles[i] = (elements[i]);
    }
    env->ReleaseDoubleArrayElements(jDoubleArray, elements, 0);
    return doubles;
}

std::shared_ptr<std::vector<double>> generateSegment(int function, std::vector<double> args) {
    std::shared_ptr<std::vector<double>> segment;
    double time = args[args.size() - 1];
    int numSamples = static_cast<int>(static_cast<double>(kSampleRate) * time);
    switch (function) {
        case 0:
            segment = VectorGenerator::generateLinearSegment(
                    args[0],
                    args[1],
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
    sustainFunctionArgs[2] = sustainFunctionArgs[1];
    sustainFunctionArgs[1] = sustainFunctionArgs[0];
    sustainFunctionArgs[0] = attackFunctionArgs[1];
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
    releaseFunctionArgs[2] = releaseFunctionArgs[1];
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

    (*envelopes)[0][0] = amplitudeEnvelope;
    (*envelopes)[1][0] = frequencyEnvelope;

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
){
    double min = (*envelopes)[row][col]->getMin();
    double max = (*envelopes)[row][col]->getMax();
    double size = (*envelopes)[row][col]->getSize();
    return (size / kSampleRate) / (max - min);
}

extern "C" JNIEXPORT void JNICALL
Java_io_fourth_1finger_sound_1sculptor_FunctionView_nativeDraw(
        JNIEnv *env,
        jobject clazz,
        jobject canvas,
        int row,
        int col
) {
    ANativeWindow* window = ANativeWindow_fromSurface(env, canvas);
    if (window != nullptr) {
        ANativeWindow_acquire(window);

        int32_t width = ANativeWindow_getWidth(window);
        int32_t height = ANativeWindow_getHeight(window);

        ANativeWindow_Buffer buffer;
        if (ANativeWindow_lock(window, &buffer, nullptr) < 0) {
            // Blue
            uint32_t lineColor = 0xFF006699;

            std::shared_ptr<Envelope> envelope = (*envelopes)[row][col];
            double minY = envelope->getMin();
            double maxY = envelope->getMax();
            double yScale = height / (maxY - minY);

            // Add room around constant
            if (minY == maxY) {
                minY += 1.0;
                maxY += 1.0;
            }

            // Draw
            int increment = envelope->getSize() / width;
            for (int x = 0; x <= width; ++x) {
                int y =  static_cast<int>((*envelope)[x * increment] * yScale);
                if (x >= 0 && x < width && y >= 0 && y < height) {
                    uint32_t* pixelPtr =
                            reinterpret_cast<uint32_t*>(buffer.bits) + y * buffer.stride + x;
                    *pixelPtr = lineColor;
                }
            }

            // Unlock the window's canvas
            ANativeWindow_unlockAndPost(window);

            // Release the window's canvas
            ANativeWindow_release(window);
        } else {
            // TODO error
            ANativeWindow_release(window);
        }

    } else {
        // TODO error
    }

}