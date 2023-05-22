#ifndef SOUNDSCULPTOR_JNIUTIL_H
#define SOUNDSCULPTOR_JNIUTIL_H

#include <jni.h>

std::vector<double> convertToArray(JNIEnv *env, jdoubleArray jDoubleArrayArgs);

int getEnumValue(JNIEnv *env, jobject enumObject);

#endif //SOUNDSCULPTOR_JNIUTIL_H
