package io.fourth_finger.sound_sculptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Used to display one envelope graph.
 */
class MainRecyclerViewAdapter:
    RecyclerView.Adapter<MainRecyclerViewAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.main_recycler_view_view_holder,
                parent,
                false
            )
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return getNumEnvelopes() + 2
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val envelopeTypeValues = Envelope.EnvelopeType.values()
        val numRows = envelopeTypeValues.size
        val envelopeType = Envelope.EnvelopeType.values()[position % numRows]

        val column = position.floorDiv(numRows)
        holder.functionView.update(envelopeType, column)
    }

    // TODO Split the amplitude and frequency envelopes
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val functionView: FunctionView

        init {
            functionView = view.findViewById(R.id.function_view)
        }

    }

}