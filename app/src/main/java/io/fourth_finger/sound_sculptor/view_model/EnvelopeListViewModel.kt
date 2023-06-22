package io.fourth_finger.sound_sculptor.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.fourth_finger.sound_sculptor.EnvelopeRepository
import kotlinx.coroutines.launch

/**
 * The view model for envelope list fragment.
 *
 * @param envelopeRepository The repository for the envelopes.
 */
class EnvelopeListViewModel(
    private val envelopeRepository: EnvelopeRepository
): ViewModel() {

    val envelopeNames: LiveData<Set<String>> = envelopeRepository.envelopeNames

    /**
     * Loads the names of saved envelopes.
     *
     * @param context The context needed to load the envelope names.
     */
    fun loadEnvelopeNames(context: Context) {
        viewModelScope.launch {
            envelopeRepository.loadEnvelopeNames(context)
        }
    }

}

/**
 * The factory to allow the envelope repository to be injected.
 *
 * @param envelopeRepository The repository for the envelopes.
 */
class EnvelopeListViewModelFactory(
    private val envelopeRepository: EnvelopeRepository,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EnvelopeListViewModel::class.java)) {
            return EnvelopeListViewModel(
                envelopeRepository,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}