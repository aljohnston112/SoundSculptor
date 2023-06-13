package io.fourth_finger.sound_sculptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Used to display one envelope graph.
 */
class MainRecyclerViewAdapter(private val addNewCallback: (Envelope.EnvelopeType) -> Unit) :
    RecyclerView.Adapter<MainRecyclerViewAdapter.MainViewHolder>() {

    private enum class ViewHolderType(val value: Int) {
        AMPLITUDE(0),
        FREQUENCY(1),
        ADD_NEW(2)
    }

    private val numRows = 2

    override fun getItemViewType(position: Int): Int {
        return getFunctionViewType(position).value
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MainViewHolder {
        return when (ViewHolderType.values().firstOrNull { it.value == viewType }) {
            ViewHolderType.AMPLITUDE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.main_recycler_view_view_holder,
                        parent,
                        false
                    )
                ViewHolderAmplitude(view)
            }

            ViewHolderType.FREQUENCY -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.main_recycler_view_view_holder,
                        parent,
                        false
                    )
                ViewHolderFrequency(view)
            }

            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.main_recycler_view_view_holder_add,
                        parent,
                        false
                    )
                ViewHolderAdd(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return getNumEnvelopes() + 2
    }

    private val amplitudeMinMaxSubject = MinMaxSubject(MinMax(Float.MAX_VALUE, Float.MIN_VALUE))
    private val frequencyMinMaxSubject = MinMaxSubject(MinMax(Float.MAX_VALUE, Float.MIN_VALUE))

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        if (holder is ViewHolderAdd) {
            holder.addFunctionView.setOnClickListener {
                addNewCallback(getEnvelopeType(position))
            }
        }
    }

    override fun onViewAttachedToWindow(holder: MainViewHolder) {
        super.onViewAttachedToWindow(holder)
        val position = holder.adapterPosition
        val functionViewType = getFunctionViewType(position)
        val column = getColumnNumber(position)
        if (holder is ViewHolderFunctionView) {
            val envelopeType = when (functionViewType) {
                ViewHolderType.AMPLITUDE -> Envelope.EnvelopeType.AMPLITUDE
                else -> Envelope.EnvelopeType.FREQUENCY
            }
            when (functionViewType) {
                ViewHolderType.AMPLITUDE -> {
                    holder.functionView.update(envelopeType, column, amplitudeMinMaxSubject)
                    amplitudeMinMaxSubject.attach(holder.functionView)
                }

                else -> {
                    holder.functionView.update(envelopeType, column, frequencyMinMaxSubject)
                    frequencyMinMaxSubject.attach(holder.functionView)
                }
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: MainViewHolder) {
        super.onViewDetachedFromWindow(holder)
        val position = holder.adapterPosition
        val functionViewType = getFunctionViewType(position)
        val column = getColumnNumber(position)
        if (holder is ViewHolderFunctionView) {
            val envelopeType = when (functionViewType) {
                ViewHolderType.AMPLITUDE -> Envelope.EnvelopeType.AMPLITUDE
                else -> Envelope.EnvelopeType.FREQUENCY
            }
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



    private fun getFunctionViewType(position: Int): ViewHolderType {
        return if (position > getNumEnvelopes() || position == getFirstRowItemCount() - 1) {
            ViewHolderType.ADD_NEW
        } else {
            when (getEnvelopeType(position)) {
                Envelope.EnvelopeType.AMPLITUDE -> {
                    ViewHolderType.AMPLITUDE
                }

                else -> {
                    ViewHolderType.FREQUENCY
                }
            }
        }
    }

    fun getFirstRowItemCount(): Int {
        return getNumAmplitudeEnvelopes() + 1
    }

    class ViewHolderAdd(view: View) : MainViewHolder(view) {
        val addFunctionView: AddFunctionView

        init {
            addFunctionView = view.findViewById(R.id.function_view)
        }
    }

    class ViewHolderFrequency(view: View) : ViewHolderFunctionView(view)
    class ViewHolderAmplitude(view: View) : ViewHolderFunctionView(view)
    open class ViewHolderFunctionView(view: View) : MainViewHolder(view) {
        val functionView: FunctionView

        init {
            functionView = view.findViewById(R.id.function_view)
        }

    }

    open class MainViewHolder(view: View) : RecyclerView.ViewHolder(view)

}