package io.fourth_finger.sound_sculptor

import java.nio.Buffer

//external fun pushBackAmplitudeEnvelope(
//    functionEnumArray: Array<Envelope.FunctionType>,
//    functionArguments: Array<Envelope.LinearFunctionParameters>
//)
//
//external fun pushBackFrequencyEnvelope(
//    functionEnumArray: Array<Envelope.FunctionType>,
//    functionArguments: Array<Envelope.LinearFunctionParameters>
//)

external fun setAmplitudeEnvelope(
    functionEnumArray: Array<Envelope.FunctionType>,
    functionArguments: Array<DoubleArray>
)

external fun setFrequencyEnvelope(
    functionEnumArray: Array<Envelope.FunctionType>,
    functionArguments: Array<DoubleArray>
)

external fun getSeconds(
    envelopeType: Envelope.EnvelopeType,
    col: Int
): Double

external fun getGraph(
    byteBuffer: Buffer,
    envelopeType: Envelope.EnvelopeType,
    col: Int,
    width: Int,
): FloatArray

external fun isValidPosition(
    envelopeType: Envelope.EnvelopeType,
    col: Int
): Boolean

external fun getEnvelopeType(pos: Int): Envelope.EnvelopeType

external fun getColumnNumber(pos: Int): Int

external fun init()

external fun getNumAmplitudeEnvelopes(): Int

external fun getNumEnvelopes(): Int

external fun startPlaying()

external fun triggerRelease()

external fun stopPlaying()
