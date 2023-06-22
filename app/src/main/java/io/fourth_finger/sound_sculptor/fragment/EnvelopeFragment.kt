package io.fourth_finger.sound_sculptor.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputEditText
import io.fourth_finger.sound_sculptor.data_class.Envelope
import io.fourth_finger.sound_sculptor.MainApplication
import io.fourth_finger.sound_sculptor.MainLayoutManager
import io.fourth_finger.sound_sculptor.adapter.MainRecyclerViewAdapter
import io.fourth_finger.sound_sculptor.R
import io.fourth_finger.sound_sculptor.databinding.FragmentMainBinding
import io.fourth_finger.sound_sculptor.view_model.EnvelopeViewModel
import io.fourth_finger.sound_sculptor.view_model.EnvelopeViewModelFactory

/**
 * A fragment for building a frequency and amplitude envelope out of envelope segments.
 */
class EnvelopeFragment : Fragment() {

    // TODO create a toggle for playing and stopping

    private val args: EnvelopeFragmentArgs by navArgs()
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EnvelopeViewModel by activityViewModels(
        factoryProducer = {
            EnvelopeViewModelFactory(
                (requireActivity().application as MainApplication).envelopeRepository
            )
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        // View model is accessible starting here
        val name = args.envelopeName

        // "null" is the default value specifying a new envelope
        if (name != "null") {
            viewModel.loadEnvelope(requireContext(), name) {
                if (it) {
                    binding.mainRecyclerView.adapter?.notifyDataSetChanged()
                }
            }
        }

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

        // Set up menu
        (requireActivity()).addMenuProvider(
            envelopeMenuProvider,
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )

        // Set up recycler view
        binding.mainRecyclerView.layoutManager = MainLayoutManager(requireContext())
        binding.mainRecyclerView.adapter = MainRecyclerViewAdapter(onCreateNewEnvelopeSegment)
    }

    /**
     * Adds and removes the save button to the menu
     */
    private val envelopeMenuProvider = object : MenuProvider {

        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.menu, menu)
            menu.findItem(R.id.action_save).isVisible = true
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            var handled = false

            val onSaveButtonClicked: (String?) -> Unit =  {
                try {
                    viewModel.trySave(requireContext(), it)
                } catch (e: FileAlreadyExistsException) {
                    Toast.makeText(
                        context,
                        "An envelope with that name already exists.\n THE FILE WAS NOT SAVED!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            when (menuItem.itemId) {
                R.id.action_save -> {
                    val dialog = getAskUserForEnvelopeNameAlertDialog(onSaveButtonClicked)
                    dialog.show()
                    handled = true
                }
            }
            return handled
        }

        /**
         * Gets the alert dialog that asks the user for a name to save the current envelope as.
         *
         * @param onSaveClicked Called with the string the user entered before clicking save.
         */
        private fun getAskUserForEnvelopeNameAlertDialog(onSaveClicked: (String?) -> Unit): AlertDialog {
            val builder = AlertDialog.Builder(requireContext())
            val dialogView = requireActivity().layoutInflater.inflate(
                R.layout.dialog_get_name,
                null
            )
            builder.setView(dialogView)
                .setPositiveButton(R.string.save) { _, _ ->
                    val nameEditText = dialogView.findViewById<TextInputEditText>(
                        R.id.TextInputFieldName
                    )
                    onSaveClicked(nameEditText.text?.toString())
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }
            return builder.create()
        }

        override fun onMenuClosed(menu: Menu) {
            super.onMenuClosed(menu)
            menu.findItem(R.id.action_save).isVisible = false
        }

    }

    /**
     * Navigates to the envelope segment creator fragment when
     * a new envelope is requested to be made.
     */
    private val onCreateNewEnvelopeSegment: (Envelope.EnvelopeType) -> Unit = {
        when (it) {
            Envelope.EnvelopeType.AMPLITUDE -> {
                requireView().findNavController().navigate(
                    EnvelopeFragmentDirections.actionEnvelopeFragmentToEnvelopeSegmentFragment(true)
                )
            }

            else -> {
                requireView().findNavController().navigate(
                    EnvelopeFragmentDirections.actionEnvelopeFragmentToEnvelopeSegmentFragment(false)
                )
            }
        }
        // TODO
    }

}