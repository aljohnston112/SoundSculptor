#ifndef SOUNDSCULPTOR_ENVELOPE_H
#define SOUNDSCULPTOR_ENVELOPE_H

#include <vector>

#include "VectorGenerator.h"

class Envelope;

std::shared_ptr<Envelope> make_envelope(
        std::vector<FunctionType> functions,
        std::vector<std::vector<double>> functionArguments,
        int64_t sampleRate
);

class Envelope {
public:

    enum EnvelopeType {
        AMPLITUDE = 0,
        FREQUENCY = 1
    };

    /**
     * @brief Constructs an Envelope instance.
     *
     * @param attack The attack phase of the envelope.
     * @param sustain The sustain phase of the envelope.
     * @param release The release phase of the envelope.
     * @param loopSustain Flag indicating whether the sustain phase should loop.
     */
    Envelope(std::shared_ptr<std::vector<double>> attack,
             std::shared_ptr<std::vector<double>> sustain,
             std::shared_ptr<std::vector<double>> release,
             bool loopSustain
    );

/**
     * @brief Gets the current value of the envelope.
     *
     * @return The current value of the envelope.
     */
    double nextDouble();

    /**
     * @brief Indicates that the envelope should transition to the release phase.
     */
    void triggerRelease();

    /**
     * @brief Resets the state of the envelope.
     *        This is typically used to reset the envelope for a new note or sound event.
     */
    void resetState();

    bool finished() const;

    double operator[](int index) const;

    double getMin(){ return min; }

    double getMax(){ return max; }

    int size(){ return attack->size() + sustain->size() + release->size(); }

private:
    std::shared_ptr<std::vector<double>> attack;
    std::shared_ptr<std::vector<double>> sustain;
    std::shared_ptr<std::vector<double>> release;
    int currentIndex = 0;
    bool loopSustain;
    bool releaseTriggered = false;

    double min;
    double max;
};

#endif // SOUNDSCULPTOR_ENVELOPE_H

