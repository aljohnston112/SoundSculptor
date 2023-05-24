package io.fourth_finger.sound_sculptor

/**
 * Contains enums for specifying envelope details.
 */
class Envelope {

    /**
     * Types of values an envelope can represent.
     */
    enum class EnvelopeType(val value: Int){
        AMPLITUDE(0),
        FREQUENCY(1)
    }

    /**
     * Shapes an envelope can take.
     */
    enum class FunctionType(val value: Int){
        LINEAR(0),
        QUADRATIC(1)
    }

    data class LinearFunctionParameters(
        val startY :Double,
        val endY :Double,
        val seconds :Double
    )

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