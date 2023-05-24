#ifndef SOUNDSCULPTOR_JNIUTIL_H
#define SOUNDSCULPTOR_JNIUTIL_H

#include <jni.h>
#include "Envelope.h"

std::vector<FunctionType> convertToFunctionTypeVector(
        JNIEnv *env,
        jobjectArray functionEnumArray
);

std::vector<std::vector<double>> convertTo2DVector(
        JNIEnv *env,
        jobjectArray jArray
);

std::vector<jobject> convertToVector(JNIEnv *env, jobjectArray jArray);

std::vector<double> convertToVector(JNIEnv *env, jdoubleArray jArray);

int getEnumValue(JNIEnv *env, jobject enumObject);

#endif //SOUNDSCULPTOR_JNIUTIL_H
