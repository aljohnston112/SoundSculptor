#include <vector>
#include "../include/JNIUtil.h"

std::vector<double> convertToArray(JNIEnv *env, jdoubleArray jDoubleArrayArgs) {
    std::vector<double> doubles(env->GetArrayLength(jDoubleArrayArgs));
    jdouble *elements = env->GetDoubleArrayElements(jDoubleArrayArgs, nullptr);
    for (int i = 0; i < env->GetArrayLength(jDoubleArrayArgs); ++i) {
        doubles[i] = (elements[i]);
    }
    env->ReleaseDoubleArrayElements(jDoubleArrayArgs, elements, 0);
    return doubles;
}

int getEnumValue(JNIEnv *env, jobject enumObject) {
    jclass enumClass = env->GetObjectClass(
            enumObject
    );
    jmethodID getValueMethod = env->GetMethodID(
            enumClass,
            "getValue",
            "()I"
    );
    jint enumValue = env->CallIntMethod(
            enumObject,
            getValueMethod
    );
    return enumValue;
}