package io.fourth_finger.sound_sculptor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import io.fourth_finger.sound_sculptor.databinding.FragmentMainBinding

/**
 * A fragment for building a frequency and amplitude envelope out of envelope segments.
 */
class EnvelopeFragment : Fragment() {

    private val viewModel: EnvelopeViewModel by viewModels()

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        // View model is accessible starting here
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
        setUpMenu()
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

    private fun setUpMenu() {
        (requireActivity()).addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu, menu)
                    menu.findItem(R.id.action_save).isVisible = true
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    var handled = false
                    when (menuItem.itemId) {
                        R.id.action_save -> {

                            // TODO
                            handled = true
                        }
                    }
                    return handled
                }

                override fun onMenuClosed(menu: Menu) {
                    super.onMenuClosed(menu)
                    menu.findItem(R.id.action_save).isVisible = false
                }

            },
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )
    }

}