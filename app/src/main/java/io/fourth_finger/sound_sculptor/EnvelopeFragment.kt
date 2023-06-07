package io.fourth_finger.sound_sculptor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import io.fourth_finger.sound_sculptor.Envelope.Companion.getFunctionType
import io.fourth_finger.sound_sculptor.databinding.FragmentEnvelopeBinding

/**
 * A Fragment for creating new envelopes.
 */
class EnvelopeFragment : Fragment() {

    private val args: EnvelopeFragmentArgs by navArgs()

    private var _binding: FragmentEnvelopeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnvelopeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!args.IsAmplitude){
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
            binding.spinnerAttackFunction.adapter = adapter
            binding.spinnerSustainFunction.adapter = adapter
            binding.spinnerReleaseFunction.adapter = adapter
        }


        binding.appCompatButton.setOnClickListener {
            // Retrieve selected functions from the spinner for each envelope stage
            val attackFunction = binding.spinnerAttackFunction.selectedItem?.toString()
            val sustainFunction = binding.spinnerSustainFunction.selectedItem?.toString()
            val releaseFunction = binding.spinnerReleaseFunction.selectedItem?.toString()

            // Retrieve function parameters from the text input fields for each envelope stage
            val attackStart = binding.filledTextFieldAttackStart.editText?.text?.toString()?.toDoubleOrNull()
            val attackEnd = binding.filledTextFieldAttackEnd.editText?.text?.toString()?.toDoubleOrNull()
            val attackTime = binding.filledTextFieldAttackTime.editText?.text?.toString()?.toDoubleOrNull()

            val sustainEnd = binding.filledTextFieldSustainEnd.editText?.text?.toString()?.toDoubleOrNull()
            val sustainTime = binding.filledTextFieldSustainTime.editText?.text?.toString()?.toDoubleOrNull()

            val releaseEnd = binding.filledTextFieldReleaseEnd.editText?.text?.toString()?.toDoubleOrNull()
            val releaseTime = binding.filledTextFieldReleaseTime.editText?.text?.toString()?.toDoubleOrNull()

            // TODO Perform data validation
            if (
                attackFunction != null && attackStart != null && attackEnd != null && attackTime != null &&
                sustainFunction != null && sustainEnd != null && sustainTime != null &&
                releaseFunction != null && releaseEnd != null && releaseTime != null
            ) {
                val envelopeData = EnvelopeData(
                    getFunctionType(attackFunction),
                    attackStart,
                    attackEnd,
                    attackTime,
                    getFunctionType(sustainFunction),
                    sustainEnd,
                    sustainTime,
                    getFunctionType(releaseFunction),
                    releaseEnd,
                    releaseTime
                )

                processEnvelopeData(envelopeData)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Invalid data",
                    Toast.LENGTH_SHORT
                ).show()
            }
            view.findNavController().popBackStack()
        }
    }

    private fun processEnvelopeData(envelopeData: EnvelopeData) {
        val functions = arrayOf(
            envelopeData.attackFunction,
            envelopeData.sustainFunction,
            envelopeData.releaseFunction
        )

        val functionArgs = arrayOf(
            doubleArrayOf(
                envelopeData.attackStart,
                envelopeData.attackEnd,
                envelopeData.attackTime
            ),
            doubleArrayOf(
                envelopeData.attackEnd,
                envelopeData.sustainEnd,
                envelopeData.sustainTime
            ),
            doubleArrayOf(
                envelopeData.sustainEnd,
                envelopeData.releaseEnd,
                envelopeData.releaseTime
            )
        )

        if(args.IsAmplitude) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}