#ifndef SOUNDSCULPTOR_SINEWAVEGENERATOR_H
#define SOUNDSCULPTOR_SINEWAVEGENERATOR_H

#include <cmath>

#include "AudioGenerator.h"
#include "ASREnvelope.h"
#include "Envelope.h"

/**
 * @class SineWaveGenerator
 * @brief Audio generator for generating sine waveforms.
 *
 * The SineWaveGenerator class is a derived class of AudioGenerator that
 * specializes in generating sine waveforms.
 * It takes an AudioConfig object for audio configuration,
 * and ASREnvelope objects for frequency and amplitude modulation.
 * The generateSamples() method generates samples based on
 * the provided envelopes and writes them to the output buffer.
 * The resetState() method resets the state of the generator to
 * help prevent artifacts such as clicks.
 */
class SineWaveGenerator : public AudioGenerator {
public:

    /**
     * Constructor for the SineWaveGenerator class.
     *
     * @param channelCount The number of audio channels.
     * @param sampleRate The sample rate of the audio.
     * @param frequencyEnvelope The envelope controlling the frequency modulation.
     * @param amplitudeEnvelope The envelope controlling the amplitude modulation.
     */
    explicit SineWaveGenerator(
            int channelCount,
            int sampleRate
    );

    /**
     * Generate audio samples using the current envelope settings.
     *
     * @param outputBuffer The output buffer to write the audio samples to.
     * @param numFrames The number of frames to generate.
     * @return A boolean indicating if the generation is completed (true) or ongoing (false).
     */
    bool generateSamples(float *outputBuffer, int32_t numFrames) override;

    /**
     * Set the amplitude envelope for the SineWaveGenerator.
     *
     * @param envelope The amplitude envelope to set.
     */
    void setAmplitudeEnvelope(std::shared_ptr<Envelope> envelope);

    /**
     * Set the frequency envelope for the SineWaveGenerator.
     *
     * @param envelope The frequency envelope to set.
     */
    void setFrequencyEnvelope(std::shared_ptr<Envelope> envelope);


    /**
     * Reset the state of the SineWaveGenerator. This will reset the phase and envelopes.
     */
    void resetState() override;

//    /**
//     * Trigger the release phase of the envelopes in the SineWaveGenerator.
//     */
//    void triggerRelease();


private:
    const int channelCount;
    const int sampleRate;
    std::shared_ptr<Envelope> frequencyEnvelope;
    std::shared_ptr<Envelope> amplitudeEnvelope;
    double currentPhase;
};

#endif // SOUNDSCULPTOR_SINEWAVEGENERATOR_H