#include "EnvelopeRepository.h"

EnvelopeRepository::EnvelopeRepository() {
    amplitudeEnvelopes = std::vector<std::shared_ptr<Envelope>>();
    frequencyEnvelopes = std::vector<std::shared_ptr<Envelope>>();
}

void EnvelopeRepository::push_back_amplitude_envelope(
        const std::shared_ptr<Envelope> &envelope
) {
    amplitudeEnvelopes.push_back(envelope);
}

void EnvelopeRepository::push_back_frequency_envelope(
        const std::shared_ptr<Envelope> &envelope
) {
    frequencyEnvelopes.push_back(envelope);
}

int64_t EnvelopeRepository::amplitude_envelope_size(const int column) {
    return amplitudeEnvelopes.at(column)->size();
}

int64_t EnvelopeRepository::frequency_envelope_size(const int column) {
    return frequencyEnvelopes.at(column)->size();
}

std::shared_ptr<Envelope> EnvelopeRepository::get_amplitude_envelope(const int column) {
    return amplitudeEnvelopes.at(column);
}

std::shared_ptr<Envelope> EnvelopeRepository::get_frequency_envelope(const int column) {
    return frequencyEnvelopes.at(column);
}

size_t EnvelopeRepository::get_num_amplitude_envelopes() {
    return amplitudeEnvelopes.size();
}

size_t EnvelopeRepository::get_num_frequency_envelopes() {
    return frequencyEnvelopes.size();
}

int64_t EnvelopeRepository::get_num_envelopes() {
    return get_num_amplitude_envelopes() + get_num_frequency_envelopes();
}
