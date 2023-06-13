#include "EnvelopeSegmentCache.h"
#include "SegmentedEnvelope.h"

EnvelopeSegmentCache::EnvelopeSegmentCache() {
    envelope_segments = std::vector<std::shared_ptr<Envelope>>();
}

std::unique_ptr<Envelope> EnvelopeSegmentCache::get_envelope_segments_as_envelope() {
    return std::make_unique<SegmentedEnvelope>(envelope_segments);;
}

void EnvelopeSegmentCache::push_back_envelope_segment(
        const std::shared_ptr<Envelope> &envelope
) {
    envelope_segments.push_back(envelope);
}

int64_t EnvelopeSegmentCache::size_of_combined_envelopes(const int column) {
    return envelope_segments.at(index)->size();
}

std::shared_ptr<Envelope> EnvelopeSegmentCache::get_envelope_segment(const int column) {
    return envelope_segments.at(index);
}

int64_t EnvelopeSegmentCache::get_num_envelope_segments() {
    return envelope_segments.size();
}
