package io.fourth_finger.sound_sculptor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import io.fourth_finger.sound_sculptor.Envelope.Companion.getFunctionType
import io.fourth_finger.sound_sculptor.databinding.FragmentEnvelopeBinding
import io.fourth_finger.sound_sculptor.databinding.FragmentEnvelopeSegmentBinding

class EnvelopeSegmentFragment : Fragment() {

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

            if (start != null && end != null && time != null && function != null) {
                val functions = arrayOf(getFunctionType(function))
                val functionArgs = arrayOf(
                    doubleArrayOf(
                        start,
                        end,
                        time
                    )
                )
                if (args.isAmplitude) {
                    setAmplitudeEnvelope(
                        functions,
                        functionArgs
                    )
                } else {
                    setFrequencyEnvelope(
                        functions,
                        functionArgs
                    )
                }
            }
            findNavController().popBackStack()
        }
    }

}