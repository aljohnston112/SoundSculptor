#ifndef SOUNDSCULPTOR_JNIUTIL_H
#define SOUNDSCULPTOR_JNIUTIL_H

#include <jni.h>
#include "Envelope.h"

std::shared_ptr<Envelope> make_envelope(
        JNIEnv *env,
        jobjectArray functionEnumArray,
        jobjectArray functionArgumentsArray,
        int64_t sampleRate
);
std::vector<std::vector<double>> convertTo2DVector(JNIEnv *env, jobjectArray jArray);
std::vector<jobject> convertToVector(JNIEnv *env, jobjectArray jArray);
std::vector<double> convertToVector(JNIEnv *env, jdoubleArray jArray);

int getEnumValue(JNIEnv *env, jobject enumObject);

#endif //SOUNDSCULPTOR_JNIUTIL_H
