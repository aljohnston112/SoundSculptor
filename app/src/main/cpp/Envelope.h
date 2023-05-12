#include <utility>
#include <vector>

class Envelope {
public:

    /**
     * @brief Constructs an Envelope instance.
     *
     * @param attack The attack phase of the envelope.
     * @param sustain The sustain phase of the envelope.
     * @param release The release phase of the envelope.
     * @param loopSustain Flag indicating whether the sustain phase should loop.
     */
    Envelope(
            std::vector<float> attack,
            std::vector<float> sustain,
            std::vector<float> release,
            bool loopSustain = false
    )
            : attack(std::move(attack)),
              sustain(std::move(sustain)),
              release(std::move(release)),
              loopSustain(loopSustain),
              currentIndex(0) {}

    /**
     * @brief Gets the current value of the envelope.
     *
     * @return The current value of the envelope.
     */
    float nextFloat() {
        float value;
        // Envelope is not released, handle attack and sustain
        if (currentIndex < attack.size()) {
            value = attack[currentIndex];
        } else if ((currentIndex < attack.size() + sustain.size()) ||
                   (loopSustain && !sustain.empty() && !releaseTriggered)
                ) {
            int sustainIndex = static_cast<int>(
                    (currentIndex - attack.size()) % sustain.size()
            );
            value = sustain[sustainIndex];
        } else if (loopSustain && !releaseTriggered) {
            value = attack[attack.size() - 1];
        } else {
            // Envelope is released, handle release
            int releaseIndex = static_cast<int>(
                    currentIndex - attack.size() - sustain.size()
            );
            value = release[releaseIndex];
        }
        currentIndex++;
        return value;
    }

    /**
     * @brief Indicates that the envelope should transition to the release phase.
     */
    void triggerRelease() {
        releaseTriggered = true;
    }

    /**
     * @brief Resets the state of the envelope.
     *        This is typically used to reset the envelope for a new note or sound event.
     */
    void resetState() {
        currentIndex = 0;
        releaseTriggered = false;
    }

private:
    std::vector<float> attack;
    std::vector<float> sustain;
    std::vector<float> release;
    int currentIndex = 0;
    bool loopSustain;
    bool releaseTriggered = false;

};