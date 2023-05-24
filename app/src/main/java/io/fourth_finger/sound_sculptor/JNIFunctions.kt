package io.fourth_finger.sound_sculptor

import java.nio.Buffer

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

external fun init()

external fun getNumEnvelopes(): Int

external fun startPlaying()

external fun triggerRelease()

external fun stopPlaying()
