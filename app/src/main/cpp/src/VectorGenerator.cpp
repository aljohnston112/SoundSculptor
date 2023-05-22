#include <cmath>
#include "VectorGenerator.h"

std::shared_ptr<std::vector<double>> generateSegment(
        FunctionType function,
        std::vector<double> args,
        int64_t sampleRate
) {
    std::shared_ptr<std::vector<double>> segment;
    double time;
    int numSamples;
    switch (function) {
        case FunctionType::LINEAR:
             time = args.at(2);
            numSamples = static_cast<int>(std::round(static_cast<double>(sampleRate) * time));
            segment = VectorGenerator::generateLinearSegment(
                    args.at(0),
                    args.at(1),
                    numSamples
            );
            break;
        case FunctionType::QUADRATIC:
            // TODO
            break;
    }
    return segment;
}

std::shared_ptr<std::vector<double>> VectorGenerator::generateConstantSegment(
        double constant,
        double numSamples
) {
    if (numSamples < 0) {
        throw std::invalid_argument("Invalid argument: numSamples must be non-negative");
    }

    std::shared_ptr<std::vector<double>> segment =
            std::make_shared<std::vector<double>>(numSamples);
    for (int i = 0; i < numSamples; ++i) {
        (*segment)[i] = constant;
    }
    return segment;
}

/**
 * Generates a vector of points representing a linear function defined by the parameters.
 *
 * @param start The starting y-value.
 * @param end The ending y-value.
 * @param numSamples The number of point between the start and the end inclusive.
 * @return A shared pointer to the vector of points representing the linear function.
 */
std::shared_ptr<std::vector<double>> VectorGenerator::generateLinearSegment(
        double start,
        double end,
        int numSamples
) {
    if (numSamples < 0) {
        throw std::invalid_argument("Invalid argument: numSamples must be non-negative");
    }

    std::shared_ptr<std::vector<double>> segment =
            std::make_shared<std::vector<double>>(numSamples);
    double increment = (end - start) / static_cast<double>(numSamples - 1);
    double value = start;

    for (int i = 0; i < numSamples; ++i) {
        (*segment)[i] = value;
        value += increment;
    }

    return segment;
}

/**
 * Generates a vector of points representing a quadratic function defined by the parameters.
 *
 * @param start The starting y-value.
 * @param vertex The vertex y-value.
 * @param end The ending y-value.
 * @param numSamplesToVertex The number of samples to the vertex from the start.
 * @param numSamples The total number of samples
 * @return A shared pointer to the vector of points representing the quadratic function.
 */
std::shared_ptr<std::vector<double>> VectorGenerator::generateQuadraticSegment(
        double start,
        double vertex,
        double end,
        int numSamplesToVertex,
        int numSamples
) {
    // TODO if both numSamplesToVertex and numSamples == 0
    if (numSamplesToVertex < 0) {
        throw std::invalid_argument(
                "Invalid argument: numSamplesToVertex must be non-negative"
        );
    } else if (numSamples < 0) {
        throw std::invalid_argument(
                "Invalid argument: numSamples must be non-negative"
        );
    } else if (numSamplesToVertex == numSamples) {
        throw std::invalid_argument(
                "Invalid argument: numSamples must not be equal to numSamplesToVertex"
        );
    }

    // Get the coefficients
    double a = (((vertex - start) / numSamplesToVertex) - ((start - end) / numSamples)) /
               (numSamples - numSamplesToVertex);
    double c = start;
    double b = (end - c - (a * static_cast<double>(numSamples) * static_cast<double>(numSamples))) /
               numSamples;

    // Generate the quadratic
    std::shared_ptr<std::vector<double>> segment =
            std::make_shared<std::vector<double>>(numSamples);
    double stepSize = 1.0 / (numSamples - 1);
    for (int i = 0; i < numSamples; ++i) {
        double x = i * stepSize;
        double y = (a * x * x) + (b * x) + c;
        (*segment)[i] = y;
    }
    return segment;
}



