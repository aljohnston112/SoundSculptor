package io.fourth_finger.sound_sculptor

class Envelope {

    enum class EnvelopeType(val value: Int){
        AMPLITUDE(0),
        FREQUENCY(1)
    }

    enum class FunctionType(val value: Int){
        LINEAR(0),
        QUADRATIC(1)
    }

    companion object {
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