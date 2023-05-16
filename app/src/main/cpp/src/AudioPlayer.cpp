#include "AudioPlayer.h"

AudioPlayer::AudioPlayer(
        std::shared_ptr<SineWaveGenerator> sineWaveGenerator,
        int channelCount,
        int sampleRate
) : sineWaveGenerator(std::move(sineWaveGenerator)) {

    // Configure the audio stream builder
    builder.setDirection(oboe::Direction::Output)
            ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
            ->setSharingMode(oboe::SharingMode::Shared)
            ->setFormat(oboe::AudioFormat::Float)
            ->setChannelCount(channelCount)
            ->setSampleRate(sampleRate)
            ->setCallback(this);

    // Open and start the audio stream
    oboe::Result result = builder.openStream(stream);
    if (result == oboe::Result::OK && stream != nullptr) {
        stream->start();
    }
}

void AudioPlayer::reset() {
    sineWaveGenerator->resetState();
}

oboe::DataCallbackResult AudioPlayer::onAudioReady(
        oboe::AudioStream *audioStream,
        void *audioData,
        int32_t numFrames
) {

    auto *outputBuffer = static_cast<float *>(audioData);
    bool done = sineWaveGenerator->generateSamples(outputBuffer, numFrames);
    if(done){
        sineWaveGenerator->resetState();
    }
    return done ? oboe::DataCallbackResult::Stop : oboe::DataCallbackResult::Continue;
}

AudioPlayer::~AudioPlayer() {
    if (stream != nullptr) {
        stream->stop();
        stream->close();
        stream = nullptr;
    }
}