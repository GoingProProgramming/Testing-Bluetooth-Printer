package uk.co.goingproprogramming.tbp.screens.discoverBrother

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import uk.co.goingproprogramming.tbp.R
import uk.co.goingproprogramming.tbp.components.AppBluetoothDiscovered
import uk.co.goingproprogramming.tbp.components.AppDialogDiscovering
import uk.co.goingproprogramming.tbp.components.AppScaffold
import uk.co.goingproprogramming.tbp.components.UIBluetoothDiscovered
import uk.co.goingproprogramming.tbp.printer.IPrinterBluetooth
import uk.co.goingproprogramming.tbp.ui.theme.TestingBluetoothPrinterTheme

@Composable
fun ScreenDiscoverBrother(
    viewModel: DiscoverBrotherViewModel = hiltViewModel(),
) {
    viewModel.state.observeAsState(initial = DiscoverBrotherViewModel.State()).value.apply {
        ScreenDiscoverBrotherUI(
            state = this,
            onEvent = viewModel::onEvent,
        )
    }
}

@Composable
fun ScreenDiscoverBrotherUI(
    state: DiscoverBrotherViewModel.State,
    onEvent: (DiscoverBrotherViewModel.Event) -> Unit,
) {
    BackHandler {
        onEvent(DiscoverBrotherViewModel.Event.OnBack)
    }

    if (state.loading) {
        AppDialogDiscovering()
    }

    AppScaffold(
        title = stringResource(id = R.string.discoverBrother_title),
        defaultPadding = 0.dp,
        onBack = { onEvent(DiscoverBrotherViewModel.Event.OnBack) },
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(1.dp),
        ) {
            items(state.bluetoothDiscoveredList) { uiBluetoothDiscovered ->
                AppBluetoothDiscovered(
                    modifier = Modifier
                        .clickable {
                            if (uiBluetoothDiscovered.enabled)
                                onEvent(
                                    DiscoverBrotherViewModel.Event.OnOpenPrinter(
                                        uiBluetoothDiscovered.bluetoothDiscovered
                                    )
                                )
                        },
                    uiBluetoothDiscovered = uiBluetoothDiscovered,
                )
            }
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_6A)
@Preview(showBackground = true, device = Devices.PIXEL_2)
@Composable
private fun ScreenDiscoverBrotherUIPreview() {
    TestingBluetoothPrinterTheme {
        ScreenDiscoverBrotherUI(
            state = DiscoverBrotherViewModel.State(
                bluetoothDiscoveredList = bluetoothDiscoveredList,
            ),
            onEvent = {},
        )
    }
}


@Preview(showBackground = true, device = Devices.PIXEL_6A)
@Preview(showBackground = true, device = Devices.PIXEL_2)
@Composable
private fun ScreenDiscoverBrotherUILoadingPreview() {
    TestingBluetoothPrinterTheme {
        ScreenDiscoverBrotherUI(
            state = DiscoverBrotherViewModel.State(
                loading = true,
                bluetoothDiscoveredList = bluetoothDiscoveredList,
            ),
            onEvent = {},
        )
    }
}

private val bluetoothDiscoveredList: List<UIBluetoothDiscovered> = listOf(
    UIBluetoothDiscovered(
        IPrinterBluetooth.BluetoothDiscovered(
            name = "Zebra",
            macAddress = "01:02:03:04",
        ),
        false,
    ),
    UIBluetoothDiscovered(
        IPrinterBluetooth.BluetoothDiscovered(
            name = "Brother",
            macAddress = "05:06:07:08",
        ),
        true,
    ),
    UIBluetoothDiscovered(
        IPrinterBluetooth.BluetoothDiscovered(
            name = "Bixolon",
            macAddress = "09:0A:0B:0C",
        ),
        false,
    ),
)