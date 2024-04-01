package uk.co.goingproprogramming.tbp.screens.discoverBixolon

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import uk.co.goingproprogramming.tbp.R
import uk.co.goingproprogramming.tbp.components.AppScaffold
import uk.co.goingproprogramming.tbp.ui.theme.TestingBluetoothPrinterTheme

@Composable
fun ScreenDiscoverBixolon(
    viewModel: DiscoverBixolonViewModel = hiltViewModel(),
) {
    ScreenDiscoverBixolonUI(
        onBack = {
            viewModel.onEvent(DiscoverBixolonViewModel.Event.OnBack)
        },
    )
}

@Composable
fun ScreenDiscoverBixolonUI(
    onBack: () -> Unit,
) {
    AppScaffold(
        title = stringResource(id = R.string.discoverBixolon_title),
        onBack = onBack,
    ) {
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_6A)
@Preview(showBackground = true, device = Devices.PIXEL_2)
@Composable
private fun ScreenDiscoverBixolonUIPreview() {
    TestingBluetoothPrinterTheme {
        ScreenDiscoverBixolonUI(
            onBack = {},
        )
    }
}