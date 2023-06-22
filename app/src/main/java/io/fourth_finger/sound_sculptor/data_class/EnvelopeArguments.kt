package io.fourth_finger.sound_sculptor.data_class

import java.io.Serializable

class EnvelopeArguments(
    val functionType: Array<FunctionType>,
    val functionArguments: Array<DoubleArray>
) : Serializable {

    override fun equals(other: Any?): Boolean {
        var equal = false
        if(this === other){
            equal = true
        } else if (javaClass == other?.javaClass) {
            other as EnvelopeArguments
            equal = functionType.contentEquals(other.functionType)
        }
        return equal
    }

    override fun hashCode(): Int {
        var result = functionType.contentHashCode()
        result = 31 * result + functionArguments.contentDeepHashCode()
        return result
    }


    /**
     * Shapes an envelope can take.
     */
    enum class FunctionType(val value: Int): Serializable {
        LINEAR(0),
        QUADRATIC(1)
    }


    companion object {

        /**
         * Returns the [FunctionType] enum that corresponds to a given string.
         * Valid strings are not case sensitive and include
         * Linear and Quadratic.
         *
         * @param functionString A string that represents the shape of the envelope.
         * @return The [FunctionType] that corresponds to the given string.
         */
        fun getFunctionType(functionString: String): FunctionType {
            return if(functionString.lowercase() == "linear"){
                FunctionType.LINEAR
            } else if(functionString.lowercase() == "quadratic"){
                FunctionType.QUADRATIC
            } else {
                FunctionType.LINEAR
            }
        }

    }

}
