package io.fourth_finger.sound_sculptor

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
import androidx.recyclerview.widget.StaggeredGridLayoutManager.GAP_HANDLING_NONE
import io.fourth_finger.sound_sculptor.databinding.FragmentMainBinding
import kotlin.math.abs

/**
 * A fragment for display the current envelopes.
 */
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mainRecyclerView.layoutManager = MainLayoutManager(requireContext())

        binding.mainRecyclerView.adapter = MainRecyclerViewAdapter {
            when (it) {
                Envelope.EnvelopeType.AMPLITUDE -> {
                    view.findNavController().navigate(
                        MainFragmentDirections.actionMainFragmentToEnvelopeSegmentFragment()
                    )
                }
                else -> {
                    view.findNavController().navigate(
                        MainFragmentDirections.actionMainFragmentToEnvelopeSegmentFragment(false)
                    )
                }
            }
        }
    }

}