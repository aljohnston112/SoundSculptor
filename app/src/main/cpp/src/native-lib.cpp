#include "../include/AudioPlayer.h"

std::unique_ptr<AudioPlayer> audioPlayer;
std::unique_ptr<std::vector<std::shared_ptr<Envelope>>> envelopes;

extern "C" JNIEXPORT void JNICALL
Java_io_fourth_1finger_sound_1sculptor_PlayTouchButton_startPlaying(
        JNIEnv *env,
        jobject /* this */
) {

    int one_second = kSampleRate;
    // Generate linear segments for two Envelopes
    std::shared_ptr<std::vector<double>> envelope1Values = VectorGenerator::generateLinearSegment(
            0.0,
            1.0,
            one_second
    );
    std::shared_ptr<std::vector<double>> envelope2Values = VectorGenerator::generateLinearSegment(
            1.0,
            0.0,
            one_second
    );

    std::shared_ptr<std::vector<double>> envelope3Values = VectorGenerator::generateLinearSegment(
            500,
            1000,
            one_second
    );
    std::shared_ptr<std::vector<double>> envelope4Values = VectorGenerator::generateLinearSegment(
            1000,
            750,
            one_second
    );

    // Create Envelope instances
    envelopes = std::make_unique<std::vector<std::shared_ptr<Envelope>>>();
    std::shared_ptr<Envelope> amplitude = std::make_shared<Envelope>(
            envelope1Values,
            std::make_shared<std::vector<double>>(),
            envelope2Values,
            true
    );
    envelopes->push_back(amplitude);

    std::shared_ptr<Envelope> frequency = std::make_shared<Envelope>(
            envelope3Values,
            std::make_shared<std::vector<double>>(),
            envelope4Values,
            true
    );
    envelopes->push_back(frequency);

    audioPlayer = std::make_unique<AudioPlayer>(frequency, amplitude);
}

extern "C" JNIEXPORT void JNICALL
Java_io_fourth_1finger_sound_1sculptor_PlayTouchButton_triggerRelease(
        JNIEnv *env,
        jobject /* this */
) {
    for (const std::shared_ptr<Envelope>& envelope: *envelopes) {
        envelope->triggerRelease();
    }
}

extern "C" JNIEXPORT void JNICALL
Java_io_fourth_1finger_sound_1sculptor_TitleFragment_stopPlaying(
        JNIEnv *env,
        jobject /* this */
) {
    audioPlayer.get()->reset();
    audioPlayer.reset();
    envelopes.reset();
}