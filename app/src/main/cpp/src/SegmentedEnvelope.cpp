#include "SegmentedEnvelope.h"

SegmentedEnvelope::SegmentedEnvelope(
        const std::vector<std::shared_ptr<Envelope>>& envelopes
) : currentIndex(0), envelopes() {
    min = std::numeric_limits<double>::max();
    max = std::numeric_limits<double>::min();

    for (const auto& envelope: envelopes) {
        appendEnvelope(envelope);
    }
}

bool SegmentedEnvelope::nextDouble(double *d) {
    bool done = false;
    if (!envelopes.empty()) {
        bool currentEnvelopeDone = envelopes.at(currentIndex)->nextDouble(d);
        if (currentEnvelopeDone) {
            currentIndex++;
            if (currentIndex >= envelopes.size()) {
                currentIndex = 0;
                done = true;
            }
        }
    }
    return done;
}

double SegmentedEnvelope::operator[](size_t index) const {
    bool outOfBounds = false;
    size_t envelopeIndex = 0;
    while(index > envelopes.at(envelopeIndex)->size() && !outOfBounds){
        index -= envelopes.at(envelopeIndex)->size();
        envelopeIndex++;
        outOfBounds = envelopeIndex >= envelopes.size();
    }
    return envelopes.at(envelopeIndex)->operator[](index);
}

void SegmentedEnvelope::appendEnvelope(const std::shared_ptr<Envelope>& envelope) {
    min = std::min(envelope->getMin(), min);
    max = std::max(envelope->getMax(), max);
    envelopes.push_back(envelope);
}

void SegmentedEnvelope::resetState() {
    for (auto& envelope: envelopes) {
        envelope->resetState();
    }
    currentIndex = 0;
}
