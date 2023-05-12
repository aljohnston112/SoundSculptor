#include <jni.h>
#include <oboe/Oboe.h>
#include <cmath>
#include <memory>
#include "SineWaveGenerator.h"

constexpr int kSampleRate = 44100;
constexpr int kChannelCount = 1;

/**
 * @brief AudioPlayer class that implements the oboe::AudioStreamCallback for audio playback.
 */
class AudioPlayer : public oboe::AudioStreamCallback {
public:

    /**
     * @brief Constructs an AudioPlayer instance.
     *
     * Configures and opens the audio stream for playback.
     */
    AudioPlayer(double frequency, double amplitude) :
            audioConfig{ kSampleRate, kChannelCount },
            sineWaveGenerator(
                    audioConfig,
                    frequency,
                    amplitude
            ) {

        // Configure the audio stream builder
        builder.setDirection(oboe::Direction::Output)
                ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
                ->setSharingMode(oboe::SharingMode::Shared)
                ->setFormat(oboe::AudioFormat::Float)
                ->setChannelCount(kChannelCount)
                ->setSampleRate(kSampleRate)
                ->setCallback(this);

        // Open and start the audio stream
        oboe::Result result = builder.openStream(stream);
        if (result == oboe::Result::OK && stream != nullptr) {
            stream->start();
        }
    }

    /**
    * @brief Destroys the AudioPlayer instance.
    *
    * Stops and closes the audio stream.
    */
    ~AudioPlayer() override {
        if (stream != nullptr) {
            stream->stop();
            stream->close();
            stream = nullptr;
        }
    }

    /**
     * @brief Callback function invoked when audio data is needed.
     *
     * Generates a sine wave and fills the audioData buffer with the generated samples.
     *
     * @param audioStream The audio stream invoking the callback.
     * @param audioData Pointer to the audio data buffer to fill.
     * @param numFrames The number of audio frames requested.
     *
     * @return DataCallbackResult indicating whether to continue or stop the audio stream.
     */
    oboe::DataCallbackResult onAudioReady(
            oboe::AudioStream *audioStream,
            void *audioData,
            int32_t numFrames
    ) override {

        auto *outputBuffer = static_cast<float *>(audioData);
        sineWaveGenerator.generateSamples(outputBuffer, numFrames);
        return oboe::DataCallbackResult::Continue;
    }

private:
    AudioConfig audioConfig;
    SineWaveGenerator sineWaveGenerator;
    oboe::AudioStreamBuilder builder;
    std::shared_ptr<oboe::AudioStream> stream;
};

std::unique_ptr<AudioPlayer> audioPlayer;

extern "C" JNIEXPORT void JNICALL
Java_io_fourth_1finger_sound_1sculptor_MainActivity_startPlaying(
        JNIEnv *env,
        jobject /* this */
) {
    const double frequency = 440.0;  // Frequency of the sine wave
    const double amplitude = 0.5;    // Amplitude of the sine wave
    audioPlayer = std::make_unique<AudioPlayer>(frequency, amplitude);
}

extern "C" JNIEXPORT void JNICALL
Java_io_fourth_1finger_sound_1sculptor_MainActivity_stopPlaying(
        JNIEnv *env,
        jobject /* this */
) {
    audioPlayer.reset();
}