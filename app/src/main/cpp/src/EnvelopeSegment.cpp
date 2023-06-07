#include "EnvelopeSegment.h"

#include <utility>
#include "VectorGenerator.h"

std::shared_ptr<EnvelopeSegment> make_envelope_segment(
        FunctionType functionType,
        std::vector<double> functionArgument,
        int64_t sampleRate
) {
    // Make segment
    std::shared_ptr<std::vector<double>> segment = generateSegment(
            functionType,
            std::move(functionArgument),
            sampleRate
    );

    return std::make_shared<EnvelopeSegment>(segment);
}

EnvelopeSegment::EnvelopeSegment(
        std::shared_ptr<std::vector<double>> segment
) : segment(std::move(segment)), currentIndex(0) {
    // Set min and max
    auto min_max = std::minmax_element(
            std::begin(*(this->segment)),
            std::end(*(this->segment))
    );
    if (min_max.first != std::end(*(this->segment))) {
        min = *min_max.first;
    }
    if (min_max.second != std::end(*(this->segment))) {
        max = *min_max.second;
    }
}

bool EnvelopeSegment::nextDouble(double *d) {
    (*d) = segment->at(currentIndex);
    currentIndex++;
    if (currentIndex >= segment->size()) {
        currentIndex = 0;
    }
    return currentIndex == 0;
}

double EnvelopeSegment::operator[](size_t index) const {
    return segment->at(index);
}

void EnvelopeSegment::resetState() {
    currentIndex = 0;
}
