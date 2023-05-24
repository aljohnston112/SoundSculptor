#include <utility>
#include <vector>
#include "../include/JNIUtil.h"
#include "Envelope.h"

std::vector<FunctionType> convertToFunctionTypeVector(
        JNIEnv *env,
        jobjectArray functionEnumArray
) {
    std::vector<jobject> enumElements = convertToVector(
            env,
            functionEnumArray
    );
    std::vector<FunctionType> functions{};
    for (auto &enumElement: enumElements) {
        int enumValue = getEnumValue(env, enumElement);
        auto functionType = static_cast<FunctionType>(enumValue);
        functions.push_back(functionType);
    }
    return functions;
}

std::vector<std::vector<double>> convertTo2DVector(
        JNIEnv *env,
        jobjectArray jArray
) {
    jsize arrayLength = env->GetArrayLength(jArray);
    std::vector<std::vector<double>> jobjects{};
    for (int i = 0; i < arrayLength; ++i) {
        auto nestedJArray = static_cast<jdoubleArray>(
                env->GetObjectArrayElement(jArray, i)
        );
        jobjects.push_back(convertToVector(env, nestedJArray));
    }
    return jobjects;
}

std::vector<jobject> convertToVector(JNIEnv *env, jobjectArray jArray) {
    jsize arrayLength = env->GetArrayLength(jArray);
    std::vector<jobject> jobjects(arrayLength);
    for (int i = 0; i < arrayLength; ++i) {
        jobjects[i] = env->GetObjectArrayElement(jArray, i);
    }
    return jobjects;
}

std::vector<double> convertToVector(JNIEnv *env, jdoubleArray jArray) {
    std::vector<double> doubles(env->GetArrayLength(jArray));
    jdouble *elements = env->GetDoubleArrayElements(jArray, nullptr);
    for (int i = 0; i < env->GetArrayLength(jArray); ++i) {
        doubles[i] = (elements[i]);
    }
    env->ReleaseDoubleArrayElements(jArray, elements, 0);
    return doubles;
}

int getEnumValue(JNIEnv *env, jobject enumObject) {
    jclass enumClass = env->GetObjectClass(
            enumObject
    );
    jmethodID getValueMethod = env->GetMethodID(enumClass, "getValue", "()I");
    jint enumValue = env->CallIntMethod(enumObject, getValueMethod);
    return enumValue;
}