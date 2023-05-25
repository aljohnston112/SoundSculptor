#include <exception>
#include <utility>
#include <vector>
#include "../include/JNIUtil.h"
#include "ASREnvelope.h"

/**
 * Converts an array of Java FunctionTypes to C++ FunctionTypes
 * @param functionEnumArray The Java array containing Java FunctionTypes.
 * @return A vector of FunctionTypes corresponding to the Java FunctionTypes.
 */
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

/**
 * Converts a jobjectArray representing a 2D array of doubles to a 2D vector.
 * @param jArray A jobjectArray representing a 2D array of doubles.
 * @return A 2D vector of doubles representing the jobjectArray.
 */
std::vector<std::vector<double>> convertTo2DVector(
        JNIEnv *env,
        jobjectArray jArray
) {

    jsize arrayLength = env->GetArrayLength(jArray);
    std::vector<std::vector<double>> jobjects{};
    for (int i = 0; i < arrayLength; ++i) {
        auto nestedJArray =
                env->GetObjectArrayElement(jArray, i);
        bool isDoubleArray = env->IsInstanceOf(
                nestedJArray,
                env->FindClass("[D")
        );
        if (!isDoubleArray) {
            throw std::invalid_argument(
                    "jobjectArray must be a 2D double array"
            );
        }
        jobjects.push_back(convertToVector(
                env,
                static_cast<jdoubleArray>(nestedJArray)
        ));
    }
    return jobjects;
}

/**
 * Converts a jobjectArray to a vector of jobjects.
 *
 * @param jArray The Java array of Java objects to convert.
 * @return A vector of jobjects representing the jobjectArray.
 */
std::vector<jobject> convertToVector(JNIEnv *env, jobjectArray jArray) {
    jsize arrayLength = env->GetArrayLength(jArray);
    std::vector<jobject> jobjects(arrayLength);
    for (int i = 0; i < arrayLength; ++i) {
        jobjects[i] = env->GetObjectArrayElement(jArray, i);
    }
    return jobjects;
}

/**
 * Converts a jdoubleArray to a vector of doubles.
 * @param jArray The Java array of doubles to convert.
 * @return A vector of doubles representing the jdoubleArray.
 */
std::vector<double> convertToVector(JNIEnv *env, jdoubleArray jArray) {
    std::vector<double> doubles(env->GetArrayLength(jArray));
    jdouble *elements = env->GetDoubleArrayElements(jArray, nullptr);
    for (int i = 0; i < env->GetArrayLength(jArray); ++i) {
        doubles[i] = (elements[i]);
    }
    env->ReleaseDoubleArrayElements(jArray, elements, 0);
    return doubles;
}

/**
 * Get the enum value out of a jobject that represents a enum.
 *
 * @param enumObject The Java enum to get the value out of.
 * @return The integer value of the enum.
 */
int getEnumValue(JNIEnv *env, jobject enumObject) {
    jclass enumClass = env->GetObjectClass(
            enumObject
    );
    jmethodID getValueMethod = env->GetMethodID(enumClass, "getValue", "()I");
    jint enumValue = env->CallIntMethod(enumObject, getValueMethod);
    return enumValue;
}