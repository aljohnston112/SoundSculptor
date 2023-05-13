#include "SineWaveGenerator.h"

static constexpr double kPI = M_PI;
static constexpr double kTwoPi = kPI * 2;

SineWaveGenerator::SineWaveGenerator(
        const AudioConfig &audioConfig,
        Envelope&& frequencyEnvelope,
        Envelope&& amplitudeEnvelope
) : audioConfig(audioConfig),
    frequencyEnvelope(std::move(frequencyEnvelope)),
    amplitudeEnvelope(std::move(amplitudeEnvelope)),
    currentPhase(0.0) {}

void SineWaveGenerator::generateSamples(float *outputBuffer, int32_t numFrames) {
    double frequency;
    double amplitude;

    if (reset) {
        frequencyEnvelope.resetState();
        amplitudeEnvelope.resetState();
        currentPhase = 0.0;
        reset = false;
    }

    for (int i = 0; i < numFrames; ++i) {

        frequency = frequencyEnvelope.nextDouble();
        amplitude = amplitudeEnvelope.nextDouble();

        // Update phase
        const double phaseIncrement =
                kTwoPi * frequency / static_cast<double>(audioConfig.sampleRate);
        currentPhase += phaseIncrement;
        if (currentPhase >= kTwoPi) {
            currentPhase -= kTwoPi;
        }

        // Write waveform to buffer
        auto sampleValue = static_cast<float>(sin(currentPhase) * amplitude);
        for (int channel = 0; channel < audioConfig.channelCount; channel++) {
            outputBuffer[i * audioConfig.channelCount + channel] = sampleValue;
        }
    }
}

void SineWaveGenerator::resetState() {
    reset = true;
}
