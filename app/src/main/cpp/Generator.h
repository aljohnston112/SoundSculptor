class Generator {
public:
    virtual ~Generator() = default;

    virtual void generateSamples(float *outputBuffer, int32_t numFrames) = 0;
};

