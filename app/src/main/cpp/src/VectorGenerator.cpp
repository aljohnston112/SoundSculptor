#include "VectorGenerator.h"

std::vector<double> VectorGenerator::generateLinearSegment(double start, double end, int numSamples) {
    std::vector<double> segment(numSamples);
    double increment = (end - start) / static_cast<double>(numSamples - 1);
    double value = start;

    for (int i = 0; i < numSamples; ++i) {
        segment[i] = value;
        value += increment;
    }

    return segment;
}


