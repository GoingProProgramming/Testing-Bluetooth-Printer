package uk.co.goingproprogramming.tbp.screens.brother

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import uk.co.goingproprogramming.tbp.R
import uk.co.goingproprogramming.tbp.components.AppDialogError
import uk.co.goingproprogramming.tbp.components.AppDialogLoading
import uk.co.goingproprogramming.tbp.components.AppDialogPrinting
import uk.co.goingproprogramming.tbp.components.AppScaffold
import uk.co.goingproprogramming.tbp.components.PdfViewer
import uk.co.goingproprogramming.tbp.ui.theme.TestingBluetoothPrinterTheme
import java.io.File

@Composable
fun ScreenBrother(
    viewModel: BrotherViewModel = hiltViewModel(),
) {
    viewModel.state.observeAsState(initial = BrotherViewModel.State()).value.apply {
        ScreenBrotherUI(
            state = this,
            onEvent = viewModel::onEvent,
        )
    }
}

private fun saveAssetAsFile(context: Context, assetName: String): File {
    val assetManager = context.assets
    val inputStream = assetManager.open(assetName)
    val file = File(context.cacheDir, assetName)
    file.outputStream().use { inputStream.copyTo(it) }
    return file
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScreenBrotherUI(
    state: BrotherViewModel.State,
    onEvent: (BrotherViewModel.Event) -> Unit,
) {
    BackHandler {
        onEvent(BrotherViewModel.Event.OnBack)
    }

    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        val file = saveAssetAsFile(context, "A4Test.pdf")
        onEvent(BrotherViewModel.Event.OnPdfFileChange(file))
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                onEvent(BrotherViewModel.Event.OnPdfUriChange(context, it))
            }
        },
    )

    if (state.printing) {
        AppDialogPrinting()
    }

    if (state.loading) {
        AppDialogLoading()
    }

    if (state.errorPrinting) {
        AppDialogError(
            message = stringResource(id = R.string.zebra_error_printing),
            onDismiss = { onEvent(BrotherViewModel.Event.OnErrorPrintingDismiss) },
        )
    }

    AppScaffold(
        title = state.printerName,
        //enableVerticalScroll = true,
        onBack = { onEvent(BrotherViewModel.Event.OnBack) },
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            enabled = state.pdfFile != null,
            onClick = { onEvent(BrotherViewModel.Event.OnPrintPdf) },
        ) {
            Text(
                text = stringResource(id = R.string.brother_pdfToPrint_button),
            )
        }
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = { launcher.launch("application/pdf") },
        ) {
            Text(
                text = stringResource(id = R.string.brother_pdfToPrint_tapToChange),
            )
        }
        state.pdfInputStream?.let {
            println("********** state.pdfInputStream = ${state.pdfInputStream}")
            PdfViewer(
                pdfStream = it,
            )
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_6A)
@Preview(showBackground = true, device = Devices.PIXEL_2)
@Composable
private fun ScreenBrotherUIPreview() {
    TestingBluetoothPrinterTheme {
        ScreenBrotherUI(
            state = BrotherViewModel.State(
                printerName = "Printer Name",
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_6A)
@Preview(showBackground = true, device = Devices.PIXEL_2)
@Composable
private fun ScreenBrotherUIValuesPreview() {
    TestingBluetoothPrinterTheme {
        ScreenBrotherUI(
            state = BrotherViewModel.State(
                printerName = "Printer Name",
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_2)
@Composable
private fun ScreenBrotherUIPrintingPreview() {
    TestingBluetoothPrinterTheme {
        ScreenBrotherUI(
            state = BrotherViewModel.State(
                printerName = "Printer Name",
                printing = true,
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_2)
@Composable
private fun ScreenBrotherUIErrorPreview() {
    TestingBluetoothPrinterTheme {
        ScreenBrotherUI(
            state = BrotherViewModel.State(
                printerName = "Printer Name",
                errorPrinting = true,
            ),
            onEvent = {},
        )
    }
}