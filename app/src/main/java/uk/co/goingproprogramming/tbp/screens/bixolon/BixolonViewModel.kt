package uk.co.goingproprogramming.tbp.screens.bixolon

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.co.goingproprogramming.tbp.services.IServiceNavigation
import javax.inject.Inject

@HiltViewModel
class BixolonViewModel @Inject constructor(
    private val serviceNavigation: IServiceNavigation,
) : ViewModel() {
    sealed interface Event {
        data object OnBack : Event
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.OnBack -> doBack()
        }
    }

    private fun doBack() {
        serviceNavigation.popBack()
    }
}