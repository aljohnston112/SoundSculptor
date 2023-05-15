#include "../include/AudioPlayer.h"

constexpr int kSampleRate = 44100;
constexpr int kChannelCount = 1;

std::unique_ptr<AudioPlayer> audioPlayer;
std::shared_ptr<SineWaveGenerator> sineWaveGenerator;

extern "C" JNIEXPORT void JNICALL
Java_io_fourth_1finger_sound_1sculptor_MainActivity_init(
        JNIEnv *env,
        jobject /* this */
) {
    sineWaveGenerator = std::make_shared<SineWaveGenerator>(
            kChannelCount,
            kSampleRate,
            nullptr,
            nullptr
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
    audioPlayer.get()->reset();
    audioPlayer.reset();
}

std::vector<double> convertToArray(JNIEnv *env, jdoubleArray jDoubleArray) {
    std::vector<double> doubles{};
    jdouble *elements = env->GetDoubleArrayElements(jDoubleArray, nullptr);
    for (int i = 0; i < env->GetArrayLength(jDoubleArray); ++i) {
        doubles.push_back(elements[i]);
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
    jint *enumElements = env->GetIntArrayElements(functionEnumArray, nullptr);

    int firstFunction = enumElements[0];
    auto jAttackFunctionArgs = static_cast<jdoubleArray>(
            env->GetObjectArrayElement(functionArguments, 0)
    );
    std::vector<double> attackFunctionArgs = convertToArray(env, jAttackFunctionArgs);
    std::shared_ptr<std::vector<double>> attack = generateSegment(
            firstFunction,
            attackFunctionArgs
    );

    int secondFunction = enumElements[1];
    auto jSustainFunctionArgs = static_cast<jdoubleArray>(
            env->GetObjectArrayElement(functionArguments, 1)
    );
    std::vector<double> sustainFunctionArgs = convertToArray(env, jSustainFunctionArgs);
    sustainFunctionArgs[2] = sustainFunctionArgs[1];
    sustainFunctionArgs[1] = sustainFunctionArgs[0];
    sustainFunctionArgs[0] = attackFunctionArgs[1];
    std::shared_ptr<std::vector<double>> sustain = generateSegment(
            secondFunction,
            sustainFunctionArgs
    );

    int thirdFunction = enumElements[2];
    auto jReleaseFunctionArgs = static_cast<jdoubleArray>(
            env->GetObjectArrayElement(functionArguments, 2)
    );
    std::vector<double> releaseFunctionArgs = convertToArray(env, jReleaseFunctionArgs);
    releaseFunctionArgs[2] = releaseFunctionArgs[1];
    releaseFunctionArgs[1] = releaseFunctionArgs[0];
    releaseFunctionArgs[0] = sustainFunctionArgs[1];
    std::shared_ptr<std::vector<double>> release = generateSegment(
            thirdFunction,
            releaseFunctionArgs
    );

    std::shared_ptr<Envelope> amplitudeEnvelope = std::make_shared<Envelope>(
            attack,
            sustain,
            release,
            true
    );
    sineWaveGenerator->setAmplitudeEnvelope(amplitudeEnvelope);

    env->ReleaseIntArrayElements(functionEnumArray, enumElements, 0);
}
