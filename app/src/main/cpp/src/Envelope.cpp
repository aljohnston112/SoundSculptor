#include <utility>

#include "../include/Envelope.h"

Envelope::Envelope(
        std::shared_ptr<std::vector<double>> attack,
        std::shared_ptr<std::vector<double>> sustain,
        std::shared_ptr<std::vector<double>> release,
        bool loopSustain
)
        : attack(std::move(attack)),
          sustain(std::move(sustain)),
          release(std::move(release)),
          loopSustain(loopSustain),
          currentIndex(0) {}


double Envelope::nextDouble() {
    double value;
    // Envelope is not released, handle attack and sustain
    if (currentIndex < attack->size()) {
        value = (*attack)[currentIndex];
    } else if ((currentIndex < attack->size() + sustain->size()) ||
               (loopSustain && !sustain->empty() && !releaseTriggered)
            ) {
        int sustainIndex = static_cast<int>(
                (currentIndex - attack->size()) % sustain->size()
        );
        value = (*sustain)[sustainIndex];
    } else if (loopSustain && !releaseTriggered) {
        value = (*attack)[attack->size() - 1];
    } else {
        // Envelope is released, handle release
        releaseTriggered = true;
        int releaseIndex = static_cast<int>(
                currentIndex - attack->size() - sustain->size()
        );
        value = (*release)[releaseIndex];
    }
    currentIndex++;
    if (currentIndex >= attack->size() + sustain->size() && loopSustain && !releaseTriggered){
        currentIndex = static_cast<int>(attack->size());
    } else if (currentIndex >= attack->size() + sustain->size() + release->size()) {
        currentIndex = 0;
    }

    return value;
}

void Envelope::triggerRelease() {
    releaseTriggered = true;
}

void Envelope::resetState() {
    currentIndex = 0;
    releaseTriggered = false;
}

bool Envelope::finished() const {
    return releaseTriggered && currentIndex == 0;
}

