package uk.co.goingproprogramming.tbp.screens.discoverBrother

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import uk.co.goingproprogramming.tbp.R
import uk.co.goingproprogramming.tbp.components.AppScaffold
import uk.co.goingproprogramming.tbp.ui.theme.TestingBluetoothPrinterTheme

@Composable
fun ScreenDiscoverBrother(
    viewModel: DiscoverBrotherViewModel = hiltViewModel(),
) {
    ScreenDiscoverBrotherUI(
        onBack = {
            viewModel.onEvent(DiscoverBrotherViewModel.Event.OnBack)
        },
    )
}

@Composable
fun ScreenDiscoverBrotherUI(
    onBack: () -> Unit,
) {
    AppScaffold(
        title = stringResource(id = R.string.discoverBrother_title),
        onBack = onBack,
    ) {
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_6A)
@Preview(showBackground = true, device = Devices.PIXEL_2)
@Composable
private fun ScreenDiscoverBrotherUIPreview() {
    TestingBluetoothPrinterTheme {
        ScreenDiscoverBrotherUI(
            onBack = {},
        )
    }
}