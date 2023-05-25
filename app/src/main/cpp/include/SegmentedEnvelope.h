//
// Created by aljoh on 5/25/2023.
//

#ifndef SOUNDSCULPTOR_SEGMENTEDENVELOPE_H
#define SOUNDSCULPTOR_SEGMENTEDENVELOPE_H


#include "Envelope.h"
#include <vector>

class SegmentedEnvelope : Envelope {
public:
    SegmentedEnvelope(
            const std::shared_ptr<Envelope> &envelope
    );

    bool nextDouble(double *d) override;

    double operator[](size_t index) const override;

    void appendEnvelope(const std::shared_ptr<Envelope> &envelope);

    void resetState() override;

    double getMin() override { return min; }

    double getMax() override { return max; }

    size_t size() override {
        size_t size = 0;
        for (auto &segment: *envelopes) {
            size += segment->size();
        }
        return size;
    }

private:
    std::shared_ptr<std::vector<std::shared_ptr<Envelope>>> envelopes;
    uint64_t currentIndex;
    double min;
    double max;
};


#endif //SOUNDSCULPTOR_SEGMENTEDENVELOPE_H
