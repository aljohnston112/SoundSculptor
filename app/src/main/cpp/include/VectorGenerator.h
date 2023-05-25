#ifndef SOUNDSCULPTOR_VECTORGENERATOR_H
#define SOUNDSCULPTOR_VECTORGENERATOR_H

#include <vector>

enum class FunctionType {
    LINEAR = 0,
    QUADRATIC = 1
};

std::shared_ptr<std::vector<double>> generateSegment(
        FunctionType function,
        std::vector<double> args,
        int64_t sampleRate
);

static std::shared_ptr<std::vector<double>> generateConstantSegment(
        double constant,
        double numSamples
);

/**
 * @brief Generates a linear segment of doubles from start to end.
 *
 * @param start The start value of the linear segment.
 * @param end The end value of the linear segment.
 * @param numSamples The number of samples in the linear segment.
 * @return A vector of doubles representing the linear segment.
 */
static std::shared_ptr<std::vector<double>> generateLinearSegment(
        double start,
        double end,
        int numSamples
);

static std::shared_ptr<std::vector<double>> generateQuadraticSegment(
        double start,
        double vertex,
        double end,
        int numSamplesToVertex,
        int numSamples
);

#endif //SOUNDSCULPTOR_VECTORGENERATOR_H
