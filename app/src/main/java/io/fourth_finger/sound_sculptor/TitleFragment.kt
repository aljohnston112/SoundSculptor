package io.fourth_finger.sound_sculptor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import io.fourth_finger.sound_sculptor.databinding.FragmentTitleBinding

class TitleFragment : Fragment(R.layout.fragment_title) {

    private external fun stopPlaying()

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
        binding.buttonAddAmplitudeEnvelope.setOnClickListener {
            view.findNavController().navigate(
                TitleFragmentDirections.actionTitleFragmentToEnvelopeFragment()
            )
        }
        binding.buttonAddFrequencyEnvelope.setOnClickListener {
            view.findNavController().navigate(
                TitleFragmentDirections.actionTitleFragmentToEnvelopeFragment(false)
            )
        }
        binding.stopButton.setOnClickListener {
            stopPlaying()
        }

        binding.buttonMain.setOnClickListener{
            view.findNavController().navigate(
                TitleFragmentDirections.actionTitleFragmentToMainFragment()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}