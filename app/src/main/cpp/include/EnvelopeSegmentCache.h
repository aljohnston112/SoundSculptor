#ifndef SOUNDSCULPTOR_ENVELOPESEGMENTCACHE_H
#define SOUNDSCULPTOR_ENVELOPESEGMENTCACHE_H

#include <vector>
#include "Envelope.h"

class EnvelopeSegmentCache {
public:
    EnvelopeSegmentCache();

    std::unique_ptr<Envelope> get_envelope_segments_as_envelope();

    void push_back_envelope_segment(const std::shared_ptr<Envelope>& envelope);

    int64_t size_of_combined_envelopes(const int index);

    std::shared_ptr<Envelope> get_envelope_segment(const int index);

    int64_t get_num_envelope_segments();

private:
    std::vector<std::shared_ptr<Envelope>> envelope_segments;

};

#endif //SOUNDSCULPTOR_ENVELOPESEGMENTCACHE_H
