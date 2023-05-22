#ifndef SOUNDSCULPTOR_ENVELOPEREPOSITORY_H
#define SOUNDSCULPTOR_ENVELOPEREPOSITORY_H

#include <vector>
#include "Envelope.h"

class EnvelopeRepository {
public:
    EnvelopeRepository();

    void push_back_amplitude_envelope(const std::shared_ptr<Envelope>& envelope);
    void push_back_frequency_envelope(const std::shared_ptr<Envelope>& envelope);

    int64_t amplitude_envelope_size(const int column);
    int64_t frequency_envelope_size(const int column);

    std::shared_ptr<Envelope> get_amplitude_envelope(const int column);
    std::shared_ptr<Envelope> get_frequency_envelope(const int column);

    int32_t get_num_amplitude_envelopes();
    int32_t get_num_frequency_envelopes();
    int64_t get_num_envelopes();

private:
    std::vector<std::shared_ptr<Envelope>> amplitudeEnvelopes;
    std::vector<std::shared_ptr<Envelope>> frequencyEnvelopes;

};

#endif //SOUNDSCULPTOR_ENVELOPEREPOSITORY_H
