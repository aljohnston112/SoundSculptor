package io.fourth_finger.sound_sculptor.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.fourth_finger.sound_sculptor.view.AddFunctionView
import io.fourth_finger.sound_sculptor.R
import io.fourth_finger.sound_sculptor.data_class.Envelope
import io.fourth_finger.sound_sculptor.data_class.MinMax
import io.fourth_finger.sound_sculptor.data_class.MinMaxSubject
import io.fourth_finger.sound_sculptor.getColumnNumber
import io.fourth_finger.sound_sculptor.getEnvelopeSegmentType
import io.fourth_finger.sound_sculptor.getMinMax
import io.fourth_finger.sound_sculptor.getNumAmplitudeEnvelopeSegments
import io.fourth_finger.sound_sculptor.getNumEnvelopeSegments
import io.fourth_finger.sound_sculptor.view.FunctionView

/**
 * Used to display an envelope as two graphs.
 * One graph is for the amplitude and the other is for the frequency.
 */
class MainRecyclerViewAdapter(
    private val addNewSegmentCallback: (Envelope.EnvelopeType) -> Unit
) : RecyclerView.Adapter<MainRecyclerViewAdapter.MainViewHolder>() {

    // Used to track the min and max so the graphs are displayed correctly
    private val amplitudeMinMaxSubject = MinMaxSubject(MinMax(Float.MAX_VALUE, Float.MIN_VALUE))
    private val frequencyMinMaxSubject = MinMaxSubject(MinMax(Float.MAX_VALUE, Float.MIN_VALUE))
    
    /**
     * The types the view holders this adapter can display.
     */
    private enum class ViewHolderType(val value: Int) {
        AMPLITUDE(0),
        FREQUENCY(1),
        ADD_NEW(2)
    }

    override fun getItemCount(): Int {
        // Plus 2 for the view holders that trigger creating a new envelope segment.
        return getNumEnvelopeSegments() + 2
    }
    
    override fun getItemViewType(position: Int): Int {
        return getViewHolderType(position).value
    }

    /**
     * Gets the view holder type for a specified position.
     *
     * @param position The position of the view holder.
     * @return The type of view holder that is to be at the given position.
     */
    private fun getViewHolderType(position: Int): ViewHolderType {
        return if (position > getNumEnvelopeSegments() || position == getNumAmplitudeEnvelopeSegments()) {
            ViewHolderType.ADD_NEW
        } else {
            when (getEnvelopeSegmentType(position)) {
                Envelope.EnvelopeType.AMPLITUDE -> {
                    ViewHolderType.AMPLITUDE
                }
                else -> {
                    ViewHolderType.FREQUENCY
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MainViewHolder {
        val viewHolderType = ViewHolderType.values().firstOrNull { it.value == viewType }
        val view = createViewHolderWithType(parent, viewHolderType)
        
        return when (viewHolderType) {
            ViewHolderType.AMPLITUDE -> {
                ViewHolderAmplitudeSegment(view)
            }
            ViewHolderType.FREQUENCY -> {
                ViewHolderFrequencySegment(view)
            }
            else -> {
                ViewHolderAddNewSegment(view)
            }
        }
    }

    /**
     * Creates a view holder for this adapter with the specified type.
     * 
     * @param parent The parent of the view holder.
     * @param viewHolderType The type fot he view holder to create.
     */
    private fun createViewHolderWithType(
        parent: ViewGroup, 
        viewHolderType: ViewHolderType?
    ): View {
        // Get the layout id
        val layoutId = if (
            viewHolderType == ViewHolderType.AMPLITUDE ||
            viewHolderType == ViewHolderType.FREQUENCY
        ){
            R.layout.main_recycler_view_view_holder
        } else {
            R.layout.main_recycler_view_view_holder_add
        }
        
        return LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
    }



    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        if (holder is ViewHolderAddNewSegment) {
            holder.addFunctionView.setOnClickListener {
                addNewSegmentCallback(getEnvelopeSegmentType(holder.adapterPosition))
            }
        }
    }

    override fun onViewAttachedToWindow(holder: MainViewHolder) {
        super.onViewAttachedToWindow(holder)

        val position = holder.adapterPosition
        val functionViewType = getViewHolderType(position)
        val column = getColumnNumber(position)

        if (holder is ViewHolderFunctionView) {

            val envelopeType = when (functionViewType) {
                ViewHolderType.AMPLITUDE -> Envelope.EnvelopeType.AMPLITUDE
                else -> Envelope.EnvelopeType.FREQUENCY
            }
            holder.functionView.setTypeAndColumn(envelopeType, column)

            // TODO this should update the minMax and then dispatch the update so the graph can be drawn
            when (functionViewType) {
                ViewHolderType.AMPLITUDE -> {
                    amplitudeMinMaxSubject.attach(holder.functionView)
                    val minMax = getMinMax(envelopeType, column)
                    amplitudeMinMaxSubject.updateMinMax(MinMax(minMax[0], minMax[1]))
                }

                else -> {
                    frequencyMinMaxSubject.attach(holder.functionView)
                    val minMax = getMinMax(envelopeType, column)
                    frequencyMinMaxSubject.updateMinMax(MinMax(minMax[0], minMax[1]))
                }
            }
        }

    }

    override fun onViewDetachedFromWindow(holder: MainViewHolder) {
        super.onViewDetachedFromWindow(holder)

        val position = holder.adapterPosition
        val functionViewType = getViewHolderType(position)

        // Detach the function views from the min max subjects to prevent memory leaks
        if (holder is ViewHolderFunctionView) {
            when (functionViewType) {
                ViewHolderType.AMPLITUDE -> {
                    amplitudeMinMaxSubject.detach(holder.functionView)
                }

                else -> {
                    frequencyMinMaxSubject.detach(holder.functionView)
                }
            }
        }
    }


    /**
     * The view holder that lets the user add a new segment to the envelope.
     */
    class ViewHolderAddNewSegment(view: View) : MainViewHolder(view) {
        val addFunctionView: AddFunctionView

        init {
            addFunctionView = view.findViewById(R.id.function_view)
        }
    }

    /**
     * The view holder that displays a frequency envelope segment.
     */
    class ViewHolderFrequencySegment(view: View) : ViewHolderFunctionView(view)

    /**
     * The view holder that displays a amplitude envelope segment.
     */
    class ViewHolderAmplitudeSegment(view: View) : ViewHolderFunctionView(view)

    /**
     * A base view holder for displaying envelope segments.
     */
    open class ViewHolderFunctionView(view: View) : MainViewHolder(view) {
        val functionView: FunctionView

        init {
            functionView = view.findViewById(R.id.function_view)
        }

    }

    /**
     * A base view holder for all view holders this adapter can display.
     */
    open class MainViewHolder(view: View) : RecyclerView.ViewHolder(view)

}