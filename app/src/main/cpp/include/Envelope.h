#ifndef SOUNDSCULPTOR_ENVELOPE_H
#define SOUNDSCULPTOR_ENVELOPE_H

#include <stddef.h>

enum EnvelopeType {
    AMPLITUDE = 0,
    FREQUENCY = 1
};

class Envelope {
public:
    virtual bool nextDouble(double* d) = 0;
    virtual double operator[](size_t index) const = 0;

    virtual double getMin() = 0;
    virtual double getMax() = 0;
    virtual size_t size() = 0;
    virtual void resetState() = 0;
};


#endif //SOUNDSCULPTOR_ENVELOPE_H
