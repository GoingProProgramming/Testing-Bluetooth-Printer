package uk.co.goingproprogramming.tbp.screens.home

import android.Manifest
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uk.co.goingproprogramming.tbp.navigationGraph.Route
import uk.co.goingproprogramming.tbp.printer.IPrinterBluetooth
import uk.co.goingproprogramming.tbp.screens.ViewModelBase
import uk.co.goingproprogramming.tbp.services.IServiceNavigation
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val serviceNavigation: IServiceNavigation,
    printerBluetooth: IPrinterBluetooth,
) : ViewModelBase<HomeViewModel.State>(State()) {
    data class State(
        val permissionList: List<String> = emptyList(),
        val bluetoothAvailable: Boolean = false,
        val showBluetoothError: Boolean = true,
    )

    enum class PrinterType {
        Zebra, Brother, Bixolon,
    }

    sealed interface Event {
        data class OnOpenPrinter(val printerType: PrinterType) : Event
        data object OnDismissError : Event
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.OnOpenPrinter -> doOpenPrinter(event.printerType)
            Event.OnDismissError -> doDismissError()
        }
    }

    init {
        localState = localState.copy(
            permissionList = getPermissionList(),
        )

        viewModelScope.launch {
            val bluetoothAvailable = printerBluetooth.isAvailable()
            localState = localState.copy(
                bluetoothAvailable = bluetoothAvailable,
                showBluetoothError = !bluetoothAvailable,
            )
        }
    }

    private fun doOpenPrinter(printerType: PrinterType) {
        when (printerType) {
            PrinterType.Zebra -> serviceNavigation.open(Route.DiscoverZebra)
            PrinterType.Brother -> serviceNavigation.open(Route.DiscoverBrother)
            PrinterType.Bixolon -> serviceNavigation.open(Route.DiscoverBixolon)
        }
    }

    private fun doDismissError() {
        localState = localState.copy(
            showBluetoothError = false,
        )
    }

    private fun getPermissionList(): List<String> =
        listOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) +
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    listOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT,
                    )
                } else {
                    emptyList()
                }
}