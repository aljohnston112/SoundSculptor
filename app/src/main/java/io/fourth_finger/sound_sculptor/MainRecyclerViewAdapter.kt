package io.fourth_finger.sound_sculptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Used to display one envelope graph.
 */
class MainRecyclerViewAdapter :
    RecyclerView.Adapter<MainRecyclerViewAdapter.ViewHolderFunctionView>() {

    private val numRows = Envelope.EnvelopeType.values().size

    override fun getItemViewType(position: Int): Int {
        return getEnvelopeType(position).value
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderFunctionView {
        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.main_recycler_view_view_holder,
                parent,
                false
            )


        return when (Envelope.EnvelopeType.values().first { it.value == viewType }) {
            Envelope.EnvelopeType.AMPLITUDE -> {
                ViewHolderAmplitude(view)
            }

            else -> {
                ViewHolderFrequency(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return getNumEnvelopes() + 2
    }

    override fun onBindViewHolder(holder: ViewHolderFunctionView, position: Int) {
        val envelopeType = getEnvelopeType(position)
        val column = position.floorDiv(numRows)
        holder.functionView.update(envelopeType, column)
    }

    private fun getEnvelopeType(position: Int): Envelope.EnvelopeType {
        return when (position % numRows) {
            0 -> {
                Envelope.EnvelopeType.AMPLITUDE
            }

            else -> {
                Envelope.EnvelopeType.FREQUENCY
            }
        }
    }

    class ViewHolderFrequency(view: View) : ViewHolderFunctionView(view)
    class ViewHolderAmplitude(view: View) : ViewHolderFunctionView(view)
    open class ViewHolderFunctionView(view: View) : RecyclerView.ViewHolder(view) {
        val functionView: FunctionView

        init {
            functionView = view.findViewById(R.id.function_view)
        }

    }

}