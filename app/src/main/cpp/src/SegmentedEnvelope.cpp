#include "SegmentedEnvelope.h"

SegmentedEnvelope::SegmentedEnvelope(
        const std::shared_ptr<Envelope>& envelope
) : currentIndex(0), envelopes() {
    min = envelope->getMin();
    max = envelope->getMax();
}

bool SegmentedEnvelope::nextDouble(double *d) {
    bool currentEnvelopeDone = envelopes->at(currentIndex)->nextDouble(d);
    bool done = false;
    if (currentEnvelopeDone) {
        currentIndex++;
        if (currentIndex >= envelopes->size()) {
            currentIndex = 0;
            done = true;
        }
    }
    return done;
}

double SegmentedEnvelope::operator[](size_t index) const {
    bool outOfBounds = false;
    size_t envelopeIndex = 0;
    while(index > envelopes->at(envelopeIndex)->size() && !outOfBounds){
        index -= envelopes->at(envelopeIndex)->size();
        envelopeIndex++;
        outOfBounds = envelopeIndex >= envelopes->size();
    }
    return envelopes->at(envelopeIndex)->operator[](index);
}

void SegmentedEnvelope::appendEnvelope(const std::shared_ptr<Envelope>& envelope) {
    envelopes->push_back(envelope);
}

void SegmentedEnvelope::resetState() {
    for (auto& envelope: *envelopes) {
        envelope->resetState();
    }
    currentIndex = 0;
}
