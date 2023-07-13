#ifndef SOUNDSCULPTOR_AUDIOPLAYER_H
#define SOUNDSCULPTOR_AUDIOPLAYER_H

#include "oboe/Oboe.h"
#include <cmath>
#include "../include/ASREnvelope.h"
#include "../include/SineWaveGenerator.h"
#include "../include/VectorGenerator.h"

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
    AudioPlayer(
            std::shared_ptr<AudioGenerator> sineWaveGenerator,
            int channelCount,
            int sampleRate
    );

    void reset();

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
    ) override;

    /**
    * @brief Stops and closes the audio stream.
    */
    ~AudioPlayer() override;

private:
    std::shared_ptr<AudioGenerator> audioGenerator;
    oboe::AudioStreamBuilder builder;
    std::shared_ptr<oboe::AudioStream> stream;
};

#endif //SOUNDSCULPTOR_AUDIOPLAYER_H
