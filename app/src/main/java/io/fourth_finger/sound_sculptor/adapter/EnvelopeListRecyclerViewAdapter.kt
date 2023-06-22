package io.fourth_finger.sound_sculptor.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import io.fourth_finger.sound_sculptor.R

/**
 * The recycler view adapter for displaying envelope names.
 *
 * @param names An initial list of envelope names.
 * @param onViewHolderClicked A lambda that is called when a view holder is clicked.
 *                            The parameter is the envelope name contained in the view holder.
 */
class EnvelopeListRecyclerViewAdapter(
    private var names: List<String>,
    private val onViewHolderClicked: (String) -> Unit
) :
    RecyclerView.Adapter<EnvelopeListRecyclerViewAdapter.EnvelopeListViewHolder>() {

    /**
     * Update the names.
     */
    fun updateNames(new_names: List<String>) {
        val diffUtilCallback = EnvelopeDiffUtilCallback(names, new_names)
        val diff = DiffUtil.calculateDiff(diffUtilCallback)
        names = new_names
        diff.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EnvelopeListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.view_holder_envelope,
                parent,
                false
            )
        return EnvelopeListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return names.size
    }

    override fun onBindViewHolder(holder: EnvelopeListViewHolder, position: Int) {
        holder.textView.text = names[position]

        // Call lambda when a view holder is clicked
        holder.rootView.setOnClickListener {
            onViewHolderClicked(names[position])
        }
    }

    class EnvelopeListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val rootView: ConstraintLayout
        val textView: MaterialTextView

        init {
            rootView = view.findViewById(R.id.constraintLayoutViewHolderEnvelope)
            textView = view.findViewById(R.id.textViewEnvelopeName)
        }

    }

    class EnvelopeDiffUtilCallback(
        private val oldList: List<String>,
        private val newList: List<String>,
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

    }

}