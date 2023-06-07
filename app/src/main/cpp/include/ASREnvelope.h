#ifndef SOUNDSCULPTOR_ASRENVELOPE_H
#define SOUNDSCULPTOR_ASRENVELOPE_H

#include <vector>

#include "VectorGenerator.h"
#include "Envelope.h"

class ASREnvelope;

std::shared_ptr<ASREnvelope> make_asr_envelope(
        std::vector<FunctionType> functionTypes,
        std::vector<std::vector<double>> functionArguments,
        int64_t sampleRate
);

class ASREnvelope: public Envelope {
public:

    /**
     * @brief Constructs an ASREnvelope instance.
     *
     * @param attack The attack phase of the envelope.
     * @param sustain The sustain phase of the envelope.
     * @param release The release phase of the envelope.
     * @param loopSustain Flag indicating whether the sustain phase should loop.
     */
    ASREnvelope(std::shared_ptr<std::vector<double>> attack,
                std::shared_ptr<std::vector<double>> sustain,
                std::shared_ptr<std::vector<double>> release,
                bool loopSustain
    );

    bool nextDouble(double *d) override;

    /**
     * @brief Indicates that the envelope should transition to the release phase.
     */
    void triggerRelease();

    /**
     * @brief Resets the state of the envelope.
     *        This is typically used to reset the envelope for a new note or sound event.
     */
    void resetState() override;

    double operator[](size_t index) const override;

    double getMin() override { return min; }

    double getMax() override { return max; }

    size_t size() override { return attack->size() + sustain->size() + release->size(); }

private:
    std::shared_ptr<std::vector<double>> attack;
    std::shared_ptr<std::vector<double>> sustain;
    std::shared_ptr<std::vector<double>> release;
    int currentIndex = 0;
    bool loopSustain;
    bool releaseTriggered = false;

    double min;
    double max;

    double nextDouble();

};

#endif // SOUNDSCULPTOR_ASRENVELOPE_H

