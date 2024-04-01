package uk.co.goingproprogramming.tbp.screens.home

import android.Manifest
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.co.goingproprogramming.tbp.navigationGraph.Route
import uk.co.goingproprogramming.tbp.screens.ViewModelBase
import uk.co.goingproprogramming.tbp.services.IServiceNavigation
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val serviceNavigation: IServiceNavigation,
) : ViewModelBase<HomeViewModel.State>(State()) {
    data class State(
        val permissionList: List<String> = emptyList(),
    )

    enum class PrinterType {
        Zebra, Brother, Bixolon,
    }

    sealed interface Event {
        data class OnOpenPrinter(val printerType: PrinterType) : Event
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.OnOpenPrinter -> doOpenPrinter(event.printerType)
        }
    }

    init {
        localState = localState.copy(
            permissionList = getPermissionList()
        )
    }

    private fun doOpenPrinter(printerType: PrinterType) {
        when (printerType) {
            PrinterType.Zebra -> serviceNavigation.open(Route.DiscoverZebra)
            PrinterType.Brother -> serviceNavigation.open(Route.DiscoverBrother)
            PrinterType.Bixolon -> serviceNavigation.open(Route.DiscoverBixolon)
        }
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