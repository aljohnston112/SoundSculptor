package io.fourth_finger.sound_sculptor

class Envelope {

    enum class EnvelopeType(val value: Int){
        AMPLITUDE(0),
        FREQUENCY(1)
    }

    companion object {
        public fun getIntForFunction(function: String): Int {
            return when(function){
                "Linear" -> 0
                else -> 0
            }
        }
    }

}