#include <cmath>
#include "AudioConfig.h"
#include "Generator.h"
#include "Envelope.h"

static float constexpr kPI = M_PI;
static float constexpr kTwoPi = kPI * 2;

class SineWaveGenerator : public Generator {
public:
    explicit SineWaveGenerator(
            const AudioConfig &audioConfig,
            Envelope frequencyEnvelope,
            Envelope amplitudeEnvelope
    ) : audioConfig(audioConfig),
        frequencyEnvelope(std::move(frequencyEnvelope)),
        amplitudeEnvelope(std::move(amplitudeEnvelope)),
        currentPhase(0.0) {}

    void generateSamples(float *outputBuffer, int32_t numFrames) {
        float frequency;
        float amplitude;

        if (reset) {
            frequencyEnvelope.resetState();
            amplitudeEnvelope.resetState();
            currentPhase = 0.0;
            reset = false;
        }

        for (int i = 0; i < numFrames; ++i) {

            frequency = frequencyEnvelope.nextFloat();
            amplitude = amplitudeEnvelope.nextFloat();

            // Update phase
            const double phaseIncrement =
                    kTwoPi * frequency / static_cast<double>(audioConfig.sampleRate);
            currentPhase += phaseIncrement;
            if (currentPhase >= kTwoPi) {
                currentPhase -= kTwoPi;
            }

            // Write waveform to buffer
            float sampleValue = static_cast<float>(sin(currentPhase) * amplitude);
            for (int channel = 0; channel < audioConfig.channelCount; channel++) {
                outputBuffer[i * audioConfig.channelCount + channel] = sampleValue;
            }
        }
    }

    void resetState() {
        reset = true;
    }

private:
    const AudioConfig &audioConfig;
    Envelope frequencyEnvelope;
    Envelope amplitudeEnvelope;
    double currentPhase;
    bool reset = false;
};