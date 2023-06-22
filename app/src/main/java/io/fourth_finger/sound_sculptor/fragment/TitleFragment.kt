package io.fourth_finger.sound_sculptor.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import io.fourth_finger.sound_sculptor.R
import io.fourth_finger.sound_sculptor.databinding.FragmentTitleBinding

/**
 * The title page of the app.
 */
class TitleFragment : Fragment(R.layout.fragment_title) {

    private var _binding: FragmentTitleBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTitleBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigate to the list of envelopes
        binding.buttonMain.setOnClickListener{
            view.findNavController().navigate(
                TitleFragmentDirections.actionTitleFragmentToEnvelopeListFragment()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}