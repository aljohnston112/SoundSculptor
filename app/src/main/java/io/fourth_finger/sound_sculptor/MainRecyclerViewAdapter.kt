package io.fourth_finger.sound_sculptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class MainRecyclerViewAdapter:
    RecyclerView.Adapter<MainRecyclerViewAdapter.ViewHolder>() {

    private external fun getNumEnvelopes(): Int

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
        return getNumEnvelopes()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val numRows = 2
        val row = position % numRows
        val col = position.floorDiv(numRows)
        holder.functionView.setPosition(row, col)
        holder.functionView.invalidate()
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val functionView: FunctionView

        init {
            functionView = view.findViewById(R.id.function_view)
        }

    }

}