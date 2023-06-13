package io.fourth_finger.sound_sculptor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import io.fourth_finger.sound_sculptor.EnvelopeArguments.Companion.getFunctionType
import io.fourth_finger.sound_sculptor.databinding.FragmentEnvelopeSegmentBinding

class EnvelopeSegmentFragment : Fragment() {

    private val viewModel: EnvelopeSegmentViewModel by viewModels()
    private val args: EnvelopeSegmentFragmentArgs by navArgs()

    private var _binding: FragmentEnvelopeSegmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnvelopeSegmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!args.isAmplitude) {
            binding.envelopeTitle.setText(R.string.frequency_envelope)
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.functions_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
            )
            binding.spinnerFunction.adapter = adapter
        }

        binding.appCompatButton.setOnClickListener {
            val function = binding.spinnerFunction.selectedItem?.toString()
            val start = binding.TextInputFieldStart.text?.toString()?.toDoubleOrNull()
            val end = binding.TextInputFieldEnd.text?.toString()?.toDoubleOrNull()
            val time = binding.TextInputFieldTime.text?.toString()?.toDoubleOrNull()
            viewModel.newEnvelopeData(args.isAmplitude, function, start, end, time)
        }

        findNavController().popBackStack()
    }
}