package uk.co.goingproprogramming.tbp.screens.discoverZebra

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import uk.co.goingproprogramming.tbp.components.UIBluetoothDiscovered
import uk.co.goingproprogramming.tbp.navigationGraph.Route
import uk.co.goingproprogramming.tbp.printer.IPrinterBluetooth
import uk.co.goingproprogramming.tbp.printer.IPrinterZebra
import uk.co.goingproprogramming.tbp.screens.ViewModelBase
import uk.co.goingproprogramming.tbp.services.IServiceNavigation
import javax.inject.Inject

@HiltViewModel
class DiscoverZebraViewModel @Inject constructor(
    private val serviceNavigation: IServiceNavigation,
    private val printerBluetooth: IPrinterBluetooth,
    private val printerZebra: IPrinterZebra,
) : ViewModelBase<DiscoverZebraViewModel.State>(State()) {
    data class State(
        val loading: Boolean = false,
        val bluetoothDiscoveredList: List<UIBluetoothDiscovered> = emptyList(),
    )

    sealed interface Event {
        data class OnOpenPrinter(val bluetoothDiscovered: IPrinterBluetooth.BluetoothDiscovered) :
            Event
        data object OnBack : Event
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.OnOpenPrinter -> doOpenPrinter(event.bluetoothDiscovered)
            is Event.OnBack -> doBack()
        }
    }

    init {
        doDiscovery()
    }

    private fun doDiscovery() {
        localState = localState.copy(
            loading = true,
            bluetoothDiscoveredList = emptyList(),
        )

        printerBluetooth.discover()
            .onEach {
                localState = localState.copy(
                    bluetoothDiscoveredList = localState.bluetoothDiscoveredList +
                            UIBluetoothDiscovered(
                                it,
                                printerZebra.isZebraPrinter(it.name),
                            )
                )
            }
            .onCompletion {
                localState = localState.copy(
                    loading = false,
                )
            }
            .launchIn(viewModelScope)
    }

    private fun doOpenPrinter(bluetoothDiscovered: IPrinterBluetooth.BluetoothDiscovered) {
        serviceNavigation.bluetoothDiscovered = bluetoothDiscovered
        serviceNavigation.open(Route.Zebra, true)
    }

    private fun doBack() {
        serviceNavigation.popBack()
    }
}