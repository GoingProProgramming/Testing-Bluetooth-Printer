package uk.co.goingproprogramming.tbp.screens.bixolon

import android.graphics.BitmapFactory
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import uk.co.goingproprogramming.tbp.R
import uk.co.goingproprogramming.tbp.components.AppDialogError
import uk.co.goingproprogramming.tbp.components.AppDialogPrinting
import uk.co.goingproprogramming.tbp.components.AppScaffold
import uk.co.goingproprogramming.tbp.extensions.saveDrawableAsBitmapOnFile
import uk.co.goingproprogramming.tbp.printer.PrinterBixolonExceptionType
import uk.co.goingproprogramming.tbp.ui.theme.TestingBluetoothPrinterTheme

@Composable
fun ScreenBixolon(
    viewModel: BixolonViewModel = hiltViewModel(),
) {
    viewModel.state.observeAsState(initial = BixolonViewModel.State()).value.apply {
        ScreenBixolonUI(
            state = this,
            onEvent = viewModel::onEvent,
        )
    }
}

@Composable
fun ScreenBixolonUI(
    state: BixolonViewModel.State,
    onEvent: (BixolonViewModel.Event) -> Unit,
) {
    BackHandler {
        onEvent(BixolonViewModel.Event.OnBack)
    }

    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        val file = R.drawable.logo_android.saveDrawableAsBitmapOnFile(context)
        onEvent(BixolonViewModel.Event.OnBitmapFileChange(file))
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                onEvent(BixolonViewModel.Event.OnBitmapUriChange(context, it))
            }
        },
    )

    if (state.printing) {
        AppDialogPrinting()
    }

    state.errorType?.let {
        val errorMessageId = when (it) {
            PrinterBixolonExceptionType.UnsupportedPrinter -> R.string.bixolon_error_printerNotSupported
            PrinterBixolonExceptionType.DeviceNotEnabled -> R.string.bixolon_error_printerNotEnabled
            PrinterBixolonExceptionType.PrintingError -> R.string.bixolon_error_printerError
        }
        AppDialogError(
            message = stringResource(id = errorMessageId),
            onDismiss = { onEvent(BixolonViewModel.Event.OnErrorPrintingDismiss) },
        )
    }

    AppScaffold(
        title = state.printerName,
        enableVerticalScroll = true,
        onBack = { onEvent(BixolonViewModel.Event.OnBack) },
    ) {
        Spacer(
            modifier = Modifier
                .weight(1f),
        )
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = stringResource(id = R.string.bixolon_imageToPrint_tapToChange),
            textAlign = TextAlign.Center,
            fontStyle = FontStyle.Italic,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            val bitmap = state.bitmapFile?.let {
                BitmapFactory.decodeFile(it.absolutePath).asImageBitmap()
            } ?: run {
                ImageBitmap.imageResource(R.drawable.logo_android)
            }
            Image(
                modifier = Modifier
                    .size(200.dp)
                    .aspectRatio(1f)
                    .clickable { launcher.launch("image/*") },
                bitmap = bitmap,
                contentDescription = stringResource(id = R.string.bixolon_image_content),
            )
        }
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            enabled = state.bitmapFile != null,
            onClick = { onEvent(BixolonViewModel.Event.OnPrintImage) },
        ) {
            Text(
                text = stringResource(id = R.string.bixolon_imageToPrint_button),
            )
        }
        Spacer(
            modifier = Modifier
                .weight(1f),
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_6A)
@Preview(showBackground = true, device = Devices.PIXEL_2)
@Composable
private fun ScreenBixolonUIPreview() {
    TestingBluetoothPrinterTheme {
        ScreenBixolonUI(
            state = BixolonViewModel.State(
                printerName = "Printer Name",
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_2)
@Composable
private fun ScreenBixolonUIPrintingPreview() {
    TestingBluetoothPrinterTheme {
        ScreenBixolonUI(
            state = BixolonViewModel.State(
                printerName = "Printer Name",
                printing = true,
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_2)
@Composable
private fun ScreenBixolonUIUnsupportedPrinterErrorPreview() {
    TestingBluetoothPrinterTheme {
        ScreenBixolonUI(
            state = BixolonViewModel.State(
                printerName = "Printer Name",
                errorType = PrinterBixolonExceptionType.UnsupportedPrinter,
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_2)
@Composable
private fun ScreenBixolonUIDeviceNotEnabledErrorPreview() {
    TestingBluetoothPrinterTheme {
        ScreenBixolonUI(
            state = BixolonViewModel.State(
                printerName = "Printer Name",
                errorType = PrinterBixolonExceptionType.DeviceNotEnabled,
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_2)
@Composable
private fun ScreenBixolonUIPrintingErrorPreview() {
    TestingBluetoothPrinterTheme {
        ScreenBixolonUI(
            state = BixolonViewModel.State(
                printerName = "Printer Name",
                errorType = PrinterBixolonExceptionType.PrintingError,
            ),
            onEvent = {},
        )
    }
}