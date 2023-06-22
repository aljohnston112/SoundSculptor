package io.fourth_finger.sound_sculptor.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import io.fourth_finger.sound_sculptor.adapter.EnvelopeListRecyclerViewAdapter
import io.fourth_finger.sound_sculptor.MainApplication
import io.fourth_finger.sound_sculptor.databinding.FragmentEnvelopeListBinding
import io.fourth_finger.sound_sculptor.view_model.EnvelopeListViewModel
import io.fourth_finger.sound_sculptor.view_model.EnvelopeListViewModelFactory

/**
 * A fragment listing the saved envelopes by name and
 * allowing the user to trigger creating a new one.
 */
class EnvelopeListFragment : Fragment() {

    private var _binding: FragmentEnvelopeListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EnvelopeListViewModel by activityViewModels(
        factoryProducer = {
            EnvelopeListViewModelFactory(
                (requireActivity().application as MainApplication).envelopeRepository
            )
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnvelopeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerViewEnvelopeList.layoutManager = LinearLayoutManager(requireContext())

        binding.recyclerViewEnvelopeList.adapter = EnvelopeListRecyclerViewAdapter(listOf()){
            // Navigate to the envelope selected by the user
            view.findNavController().navigate(
                EnvelopeListFragmentDirections.actionEnvelopeListFragmentToEnvelopeFragment(it)
            )
        }

        binding.FABCreateNew.setOnClickListener{
            // Navigate to a blank envelope
            view.findNavController().navigate(
                EnvelopeListFragmentDirections.actionEnvelopeListFragmentToEnvelopeFragment()
            )
        }

    }

    override fun onStart() {
        super.onStart()
        // View model is accessible starting here
        viewModel.loadEnvelopeNames(requireContext())

        // Update the envelope name list as names get updated
        viewModel.envelopeNames.observe(viewLifecycleOwner) {
            (binding.recyclerViewEnvelopeList.adapter as EnvelopeListRecyclerViewAdapter).updateNames(
                it.toList()
            )
        }

    }

}