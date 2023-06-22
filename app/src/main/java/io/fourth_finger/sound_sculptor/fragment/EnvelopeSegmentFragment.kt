package io.fourth_finger.sound_sculptor.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import io.fourth_finger.sound_sculptor.MainApplication
import io.fourth_finger.sound_sculptor.R
import io.fourth_finger.sound_sculptor.databinding.FragmentEnvelopeSegmentBinding
import io.fourth_finger.sound_sculptor.view_model.EnvelopeViewModel
import io.fourth_finger.sound_sculptor.view_model.EnvelopeViewModelFactory

class EnvelopeSegmentFragment : Fragment() {

    private val args: EnvelopeSegmentFragmentArgs by navArgs()
    private var _binding: FragmentEnvelopeSegmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EnvelopeViewModel by activityViewModels(
        factoryProducer = {
            EnvelopeViewModelFactory(
                (requireActivity().application as MainApplication).envelopeRepository
            )
        }
    )

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

        binding.spinnerFunction.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    TODO("Not yet implemented")
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // TODO should not happen
                    TODO("Not yet implemented")
                }

            }

        binding.appCompatButton.setOnClickListener {
            val function = binding.spinnerFunction.selectedItem?.toString()
            val start = binding.TextInputFieldStart.text?.toString()?.toDoubleOrNull()
            val end = binding.TextInputFieldEnd.text?.toString()?.toDoubleOrNull()
            val time = binding.TextInputFieldTime.text?.toString()?.toDoubleOrNull()
            if (function == "Linear" && start != null && end != null && time != null) {
                viewModel.newEnvelopeData(
                    args.isAmplitude,
                    function,
                    doubleArrayOf(start, end, time)
                )
                findNavController().popBackStack()
            }
        }

    }
}