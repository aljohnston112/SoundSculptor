package io.fourth_finger.sound_sculptor

import java.nio.Buffer

external fun addAmplitudeEnvelopeSegment(
    functionEnumArray: Array<EnvelopeArguments.FunctionType>,
    functionArguments: Array<DoubleArray>
)

external fun addFrequencyEnvelopeSegment(
    functionEnumArray: Array<EnvelopeArguments.FunctionType>,
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
