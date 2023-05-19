#include "Envelope.h"

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
          currentIndex(0) {
    auto min_max = std::minmax(
            std::begin(*this->attack),
            std::end(*this->attack)
    );
    min = *min_max.first;
    max = *min_max.second;
    auto sustain_min_max = std::minmax(
            std::begin(*this->sustain),
            std::end(*this->sustain)
    );
    min = std::max(min, *sustain_min_max.first);
    max = std::max(max, *sustain_min_max.second);
    auto release_min_max = std::minmax(
            std::begin(*this->release),
            std::end(*this->release)
    );
    min = std::max(min, *release_min_max.first);
    max = std::max(max, *release_min_max.second);
}


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
    if (currentIndex >= attack->size() + sustain->size() && loopSustain && !releaseTriggered) {
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

double Envelope::operator[](int index) const {
    if (index < attack->size()) {
        return (*attack)[index];
    } else if (index < attack->size() + sustain->size()) {
        return (*sustain)[index - attack->size()];
    } else if (index < attack->size() + sustain->size() + release->size()) {
        return (*release)[index - attack->size() - sustain->size()];
    } else {
        throw std::out_of_range("Index out of range.");
    }
}
