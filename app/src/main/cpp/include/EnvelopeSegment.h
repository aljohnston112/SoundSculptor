#ifndef SOUNDSCULPTOR_ENVELOPESEGMENT_H
#define SOUNDSCULPTOR_ENVELOPESEGMENT_H

#include <vector>
#include "Envelope.h"

class EnvelopeSegment: Envelope {
public:
    EnvelopeSegment(std::shared_ptr<std::vector<double>> segment);
    bool nextDouble(double* d) override;
    double operator[](size_t index) const override;
    void resetState() override;

    double getMin() override { return min; }
    double getMax() override { return max; }
    size_t size() override { return segment->size(); }

private:
    std::shared_ptr<std::vector<double>> segment;
    uint64_t currentIndex;
    double min;
    double max;

};


#endif //SOUNDSCULPTOR_ENVELOPESEGMENT_H
