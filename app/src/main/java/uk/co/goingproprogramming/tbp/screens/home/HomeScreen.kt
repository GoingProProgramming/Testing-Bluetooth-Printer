package uk.co.goingproprogramming.tbp.screens.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import uk.co.goingproprogramming.tbp.R
import uk.co.goingproprogramming.tbp.components.AppDialogError
import uk.co.goingproprogramming.tbp.components.AppScaffold
import uk.co.goingproprogramming.tbp.ui.theme.TestingBluetoothPrinterTheme

@Composable
fun ScreenHome(
    viewModel: HomeViewModel = hiltViewModel(),
) {
    viewModel.state.observeAsState(initial = HomeViewModel.State()).value.apply {
        ScreenHomeUI(
            state = this,
            onEvent = viewModel::onEvent,
        )
    }
}

@Composable
fun ScreenHomeUI(
    state: HomeViewModel.State,
    onEvent: (HomeViewModel.Event) -> Unit,
) {
    val permissionsRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {}
    )
    LaunchedEffect(key1 = true) {
        if (state.bluetoothAvailable)
            permissionsRequest.launch(state.permissionList.toTypedArray())
    }

    if (state.showBluetoothError) {
        AppDialogError(
            message = stringResource(id = R.string.home_errorBluetooth),
            onDismiss = { onEvent(HomeViewModel.Event.OnDismissError) },
        )
    }

    AppScaffold(
        title = stringResource(id = R.string.home_title),
    ) {
        Box(
            modifier = Modifier
                .weight(1f),
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.Center),
                text = stringResource(id = R.string.home_message),
                textAlign = TextAlign.Center,
            )
        }
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            enabled = state.bluetoothAvailable,
            onClick = { onEvent(HomeViewModel.Event.OnOpenPrinter(HomeViewModel.PrinterType.Zebra)) }
        ) {
            Text(
                text = stringResource(id = R.string.home_buttonZebra),
            )
        }
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            enabled = state.bluetoothAvailable,
            onClick = { onEvent(HomeViewModel.Event.OnOpenPrinter(HomeViewModel.PrinterType.Brother)) }
        ) {
            Text(
                text = stringResource(id = R.string.home_buttonBrother),
            )
        }
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            enabled = state.bluetoothAvailable,
            onClick = { onEvent(HomeViewModel.Event.OnOpenPrinter(HomeViewModel.PrinterType.Bixolon)) }
        ) {
            Text(
                text = stringResource(id = R.string.home_buttonBixolon),
            )
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_6A)
@Preview(showBackground = true, device = Devices.PIXEL_2)
@Composable
private fun ScreenHomeUIPreview() {
    TestingBluetoothPrinterTheme {
        ScreenHomeUI(
            state = HomeViewModel.State(
                bluetoothAvailable = true,
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_2)
@Composable
private fun ScreenHomeUIBluetoothNotAvailablePreview() {
    TestingBluetoothPrinterTheme {
        ScreenHomeUI(
            state = HomeViewModel.State(
                showBluetoothError = true,
            ),
            onEvent = {},
        )
    }
}