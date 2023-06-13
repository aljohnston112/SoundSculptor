#ifndef SOUNDSCULPTOR_AUDIOGENERATOR_H
#define SOUNDSCULPTOR_AUDIOGENERATOR_H

#include <memory>

#include "Envelope.h"

/**
 * @class AudioGenerator
 * @brief Abstract base class for audio generation objects.
 *
 * The AudioGenerator class defines an interface for generating audio samples.
 * Derived classes must implement the generateSamples() and resetState() methods.
 */
class AudioGenerator {
public:

    /**
     * @brief Generates audio samples and writes them to the output buffer.
     *
     * This method is responsible for generating audio samples and
     * writing them to the output buffer.
     *
     * @param outputBuffer Pointer to the output buffer where the generated samples will be written.
     * @param numFrames The number of frames to generate.
     * @return True if either the amplitude envelope or frequency envelope if at it's end.
     */
    virtual bool generateSamples(float *outputBuffer, int32_t numFrames) = 0;

    /**
     * @brief Resets the state of the audio generator.
     *
     * This method is responsible for resetting the state of the audio generator.
     * Derived classes must implement this method to reset any internal state variables
     * such as phase and envelope_segments when there is a reset signal.
     */
    virtual void resetState() = 0;

    virtual void setAmplitudeEnvelope(std::shared_ptr<Envelope> envelope) = 0;

    virtual void setFrequencyEnvelope(std::shared_ptr<Envelope> envelope) = 0;

    virtual ~AudioGenerator() = default;

};

#endif // SOUNDSCULPTOR_AUDIOGENERATOR_H