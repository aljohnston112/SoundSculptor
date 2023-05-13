#ifndef SOUNDSCULPTOR_VECTORGENERATOR_H
#define SOUNDSCULPTOR_VECTORGENERATOR_H

#include <vector>

/**
 * @class VectorGenerator
 * @brief A utility class for generating segments of doubles.
 *
 * The VectorGenerator class provides various static methods to
 * generate segments of doubles with a specified number of samples.
 */
class VectorGenerator {
public:

    /**
     * @brief Generates a linear segment of doubles from start to end.
     *
     * @param start The start value of the linear segment.
     * @param end The end value of the linear segment.
     * @param numSamples The number of samples in the linear segment.
     * @return A vector of doubles representing the linear segment.
     */
    static std::vector<double> generateLinearSegment(double start, double end, int numSamples);
};

#endif //SOUNDSCULPTOR_VECTORGENERATOR_H
