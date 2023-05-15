#include <utility>

#include "../include/SineWaveGenerator.h"
#include "VectorGenerator.h"

static constexpr double kPI = M_PI;
static constexpr double kTwoPi = kPI * 2;

SineWaveGenerator::SineWaveGenerator(
        int channelCount,
        int sampleRate,
        std::shared_ptr<Envelope> frequencyEnvelope,
        std::shared_ptr<Envelope> amplitudeEnvelope
) : channelCount(channelCount),
    sampleRate(sampleRate),
    frequencyEnvelope(std::move(frequencyEnvelope)),
    amplitudeEnvelope(std::move(amplitudeEnvelope)),
    currentPhase(0.0) {

    if (amplitudeEnvelope == nullptr || frequencyEnvelope == nullptr) {
        int one_second = sampleRate;
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
        if(amplitudeEnvelope == nullptr) {
            this->amplitudeEnvelope = std::make_shared<Envelope>(
                    envelope1Values,
                    std::make_shared<std::vector<double>>(),
                    envelope2Values,
                    true
            );
        }

        if(frequencyEnvelope == nullptr) {
            this->frequencyEnvelope = std::make_shared<Envelope>(
                    envelope3Values,
                    std::make_shared<std::vector<double>>(),
                    envelope4Values,
                    true
            );
        }
    }
}

bool SineWaveGenerator::generateSamples(float *outputBuffer, int32_t numFrames) {
    double frequency;
    double amplitude;
    bool done = false;

    if (reset) {
        frequencyEnvelope->resetState();
        amplitudeEnvelope->resetState();
        currentPhase = 0.0;
        reset = false;
    }

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

void SineWaveGenerator::setAmplitudeEnvelope(std::shared_ptr<Envelope> envelope) {
    amplitudeEnvelope = std::move(envelope);
}

void SineWaveGenerator::resetState() {
    reset = true;
}

void SineWaveGenerator::triggerRelease(){
    frequencyEnvelope->triggerRelease();
    amplitudeEnvelope->triggerRelease();
}
