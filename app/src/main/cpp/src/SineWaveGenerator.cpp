#include "SineWaveGenerator.h"
#include "VectorGenerator.h"

static constexpr double kPI = M_PI;
static constexpr double kTwoPi = kPI * 2;

/**
 * Constructor for the SineWaveGenerator class.
 *
 * @param channelCount The number of audio channels.
 * @param sampleRate The sample rate of the audio.
 */
SineWaveGenerator::SineWaveGenerator(
        int channelCount,
        int sampleRate
) : channelCount(channelCount),
    sampleRate(sampleRate),
    currentPhase(0.0) {}

/**
 * Generate audio samples using the current envelope settings.
 *
 * @param outputBuffer The output buffer to write the audio samples to.
 * @param numFrames The number of frames to generate.
 * @return A boolean indicating if the generation has completed (true) or
 *         there are more samples to generate (false).
 */
bool SineWaveGenerator::generateSamples(float *outputBuffer, int32_t numFrames) {
    double frequency;
    double amplitude;
    bool done = false;

    int i = 0;
    while (i < numFrames && !done) {
        frequency = frequencyEnvelope->nextDouble();
        amplitude = amplitudeEnvelope->nextDouble();
        done = frequencyEnvelope->finished() || amplitudeEnvelope->finished();


        // Update phase
        const double phaseIncrement =
                kTwoPi * frequency / static_cast<double>(sampleRate);
        currentPhase += phaseIncrement;
        if (currentPhase >= kTwoPi) {
            currentPhase -= kTwoPi;
        }

        // Write waveform to buffer
        auto sampleValue = static_cast<float>(sin(currentPhase) * amplitude);
        for (int channel = 0; channel < channelCount; channel++) {
            outputBuffer[i * channelCount + channel] = sampleValue;
        }
        i++;
    }
    return done;
}

/**
 * Set the amplitude envelope for the SineWaveGenerator.
 *
 * @param envelope The amplitude envelope to set.
 */
void SineWaveGenerator::setAmplitudeEnvelope(std::shared_ptr<Envelope> envelope) {
    amplitudeEnvelope = std::move(envelope);
}

/**
 * Set the frequency envelope for the SineWaveGenerator.
 *
 * @param envelope The frequency envelope to set.
 */
void SineWaveGenerator::setFrequencyEnvelope(std::shared_ptr<Envelope> envelope) {
    frequencyEnvelope = std::move(envelope);
}

/**
 * Reset the state of the SineWaveGenerator.
 * This will reset the phase and the envelopes.
 */
void SineWaveGenerator::resetState() {
    frequencyEnvelope->resetState();
    amplitudeEnvelope->resetState();
    currentPhase = 0.0;
}

/**
 * Trigger the release phase of the frequency and amplitude envelopes.
 */
void SineWaveGenerator::triggerRelease(){
    frequencyEnvelope->triggerRelease();
    amplitudeEnvelope->triggerRelease();
}


