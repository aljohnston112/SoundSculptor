#include "ASREnvelope.h"
#include "VectorGenerator.h"

std::shared_ptr<ASREnvelope> make_asr_envelope(
        std::vector<FunctionType> functionTypes,
        std::vector<std::vector<double>> functionArguments,
        int64_t sampleRate
) {
    // Make attack segment
    std::shared_ptr<std::vector<double>> attack = generateSegment(
            functionTypes.at(0),
            functionArguments.at(0),
            sampleRate
    );

    // Make sustain segment
    std::shared_ptr<std::vector<double>> sustain = generateSegment(
            functionTypes.at(1),
            functionArguments.at(1),
            sampleRate
    );

    // Make release segment
    std::shared_ptr<std::vector<double>> release = generateSegment(
            functionTypes.at(2),
            functionArguments.at(2),
            sampleRate
    );

    return std::make_shared<ASREnvelope>(
            attack,
            sustain,
            release,
            true
    );
}

std::shared_ptr<ASREnvelope> make_envelope(
        std::vector<FunctionType> functionTypes,
        std::vector<std::vector<double>> functionArguments,
        int64_t sampleRate
) {
    // Make attack segment
    std::shared_ptr<std::vector<double>> attack = generateSegment(
            functionTypes.at(0),
            functionArguments.at(0),
            sampleRate
    );

    // Make sustain segment
    std::shared_ptr<std::vector<double>> sustain = generateSegment(
            functionTypes.at(1),
            functionArguments.at(1),
            sampleRate
    );

    // Make release segment
    std::shared_ptr<std::vector<double>> release = generateSegment(
            functionTypes.at(2),
            functionArguments.at(2),
            sampleRate
    );

    return std::make_shared<ASREnvelope>(
            attack,
            sustain,
            release,
            true
    );
}

ASREnvelope::ASREnvelope(
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
    // Min max of attack
    auto min_max = std::minmax_element(
            std::begin(*this->attack),
            std::end(*this->attack)
    );
    if (min_max.first != std::end(*this->attack)) {
        min = *min_max.first;
    }
    if (min_max.second != std::end(*this->attack)) {
        max = *min_max.second;
    }

    // Min max of sustain
    auto sustain_min_max = std::minmax_element(
            std::begin(*this->sustain),
            std::end(*this->sustain)
    );
    if (sustain_min_max.first != std::end(*this->sustain)) {
        min = std::min(min, *sustain_min_max.first);
    }
    if (sustain_min_max.second != std::end(*this->sustain)) {
        max = std::max(max, *sustain_min_max.second);
    }

    // Min max of release
    auto release_min_max = std::minmax_element(
            std::begin(*this->release),
            std::end(*this->release)
    );
    if (release_min_max.first != std::end(*this->release)) {
        min = std::min(min, *release_min_max.first);
    }
    if (release_min_max.second != std::end(*this->release)) {
        max = std::max(max, *release_min_max.second);
    }
}

bool ASREnvelope::nextDouble(double *d) {
    (*d) = nextDouble();
    return currentIndex == 0;
}

double ASREnvelope::nextDouble() {
    double value;
    // ASREnvelope is not released, handle attack and sustain
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
        // ASREnvelope is released, handle release
        releaseTriggered = true;
        int releaseIndex = static_cast<int>(
                currentIndex - attack->size() - sustain->size()
        );
        value = (*release)[releaseIndex];
    }

    // Update the index
    currentIndex++;
    if (currentIndex >= attack->size() + sustain->size() &&
        loopSustain && !releaseTriggered) {
        // Loop sustain
        currentIndex = static_cast<int>(attack->size() + 1);
    } else if (currentIndex >=
               attack->size() + sustain->size() + release->size()) {
        // Loop whole envelope
        currentIndex = 0;
    }

    return value;
}

void ASREnvelope::triggerRelease() {
    releaseTriggered = true;
}

void ASREnvelope::resetState() {
    currentIndex = 0;
    releaseTriggered = false;
}

double ASREnvelope::operator[](size_t index) const {
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
