#ifndef SOUNDSCULPTOR_SINEWAVEGENERATOR_H
#define SOUNDSCULPTOR_SINEWAVEGENERATOR_H

#include <cmath>

#include "AudioConfig.h"
#include "AudioGenerator.h"
#include "Envelope.h"

/**
 * @class SineWaveGenerator
 * @brief Audio generator for generating sine waveforms.
 *
 * The SineWaveGenerator class is a derived class of AudioGenerator that
 * specializes in generating sine waveforms.
 * It takes an AudioConfig object for audio configuration,
 * and Envelope objects for frequency and amplitude modulation.
 * The generateSamples() method generates samples based on
 * the provided envelopes and writes them to the output buffer.
 * The resetState() method resets the state of the generator to
 * help prevent artifacts such as clicks.
 */
class SineWaveGenerator : public AudioGenerator {
public:

    /**
     * @brief Constructs a SineWaveGenerator object.
     *
     * @param audioConfig The audio configuration for the generator.
     * @param frequencyEnvelope The envelope for frequency modulation.
     * @param amplitudeEnvelope The envelope for amplitude modulation.
     */
    explicit SineWaveGenerator(
            int channelCount,
            int sampleRate,
            std::shared_ptr<Envelope> frequencyEnvelope,
            std::shared_ptr<Envelope> amplitudeEnvelope
    );

/**
     * @brief Generates audio samples based on the envelopes and writes them to the output buffer.
     *
     * This method generates audio samples based on the frequency and amplitude envelopes and writes them to the
     * specified output buffer. It overrides the generateSamples() method from the base class.
     *
     * @param outputBuffer Pointer to the output buffer where the generated samples will be written.
     * @param numFrames The number of frames to generate.
     * @return True if either the amplitude envelope or frequency envelope if at it's end.
     */
    bool generateSamples(float *outputBuffer, int32_t numFrames) override;

    /**
     * @brief Resets the state of the SineWaveGenerator.
     *
     * This method resets the state of the SineWaveGenerator,
     * including the phase and the state of the frequency and amplitude envelopes.
     */
    void resetState() override;

    void setAmplitudeEnvelope(std::shared_ptr<Envelope> envelope);

    void triggerRelease();

private:
    const int channelCount;
    const int sampleRate;
    std::shared_ptr<Envelope> frequencyEnvelope;        ///< The envelope for frequency modulation.
    std::shared_ptr<Envelope> amplitudeEnvelope;        ///< The envelope for amplitude modulation.
    double currentPhase;               ///< The current phase of the sine waveform.
    bool reset = false;                ///< Flag indicating if the generator needs to be reset.
};

#endif // SOUNDSCULPTOR_SINEWAVEGENERATOR_H