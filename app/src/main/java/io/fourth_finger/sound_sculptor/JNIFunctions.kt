package io.fourth_finger.sound_sculptor

import io.fourth_finger.sound_sculptor.data_class.Envelope
import io.fourth_finger.sound_sculptor.data_class.EnvelopeArguments
import java.nio.Buffer

/**
 * Clears the envelope caches and creates an audio generator
 */
external fun init()

/**
 * Appends amplitude envelope segments to the amplitude envelope.
 * 
 * @param functionEnumArray The function types for the segments of the envelope.
 * @param functionArguments The arguments for the functions of each segment of the envelope.
 */
external fun addAmplitudeEnvelopeSegmentArguments(
    functionEnumArray: Array<EnvelopeArguments.FunctionType>,
    functionArguments: Array<DoubleArray>
)

/**
 * Appends frequency envelope segments to the frequency envelope.
 *
 * @param functionEnumArray The function types for the segments of the envelope.
 * @param functionArguments The arguments for the functions of each segment of the envelope.
 */
external fun addFrequencyEnvelopeSegmentArguments(
    functionEnumArray: Array<EnvelopeArguments.FunctionType>,
    functionArguments: Array<DoubleArray>
)

/**
 * @return The number of amplitude envelope segments.
 */
external fun getNumAmplitudeEnvelopeSegments(): Int

/**
 * @return The total number of envelope segments.
 */
external fun getNumEnvelopeSegments(): Int

/**
 * Gets the type of an envelope segment at a given position.
 *
 * @param pos The position of the envelope segment.
 * @return The envelope type of the envelope segment at the given position.
 */
external fun getEnvelopeSegmentType(pos: Int): Envelope.EnvelopeType

/**
 * Gets the column number of the envelope segment at the given position.
 *
 * @param pos The position of the envelope segment.
 * @return The column number of the envelope segment.
 */
external fun getColumnNumber(pos: Int): Int


/**
 * Gets the min and max of the envelope segment at the specified position.
 * @param envelopeType The envelope type of the envelope segment.
 * @param column The column the envelope segment is in.
 * @return A float array with the min at index 0 and the max at index 1.
 */
external fun getMinMax(
    envelopeType: Envelope.EnvelopeType,
    column: Int
): FloatArray

/**
 * Fills a buffer with the graph of an envelope segment at a specified position.
 * @param byteBuffer The buffer to be filled.
 * @param envelopeType The type of the envelope segment.
 * @param col The column of the envelope segment.
 * @param width The number of data points to be loaded into the buffer.
 */
external fun fillBufferWithGraph(
    byteBuffer: Buffer,
    envelopeType: Envelope.EnvelopeType,
    col: Int,
    width: Int,
): FloatArray


/**
 * Checks whether the given position is a valid envelope segment index.
 *
 * @param envelopeType The type of the envelope segment.
 * @param column The column of the envelope segment.
 *
 * @return True is the position is valid else false.
 */
external fun isValidPosition(
    envelopeType: Envelope.EnvelopeType,
    column: Int
): Boolean

/**
 * Gets the time of an envelope segment in seconds.
 *
 * @param envelopeType The type of the envelope segment.
 * @param column The column of the envelope segment.
 *
 * @return The time of the envelope segment in seconds.
 */
external fun getSeconds(
    envelopeType: Envelope.EnvelopeType,
    column: Int
): Double


external fun startPlaying()

external fun triggerRelease()

external fun stopPlaying()
