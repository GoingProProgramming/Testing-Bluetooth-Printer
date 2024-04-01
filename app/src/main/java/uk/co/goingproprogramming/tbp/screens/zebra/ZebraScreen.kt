package uk.co.goingproprogramming.tbp.screens.zebra

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import uk.co.goingproprogramming.tbp.R
import uk.co.goingproprogramming.tbp.components.AppDialogError
import uk.co.goingproprogramming.tbp.components.AppDialogPrinting
import uk.co.goingproprogramming.tbp.components.AppRadioButton
import uk.co.goingproprogramming.tbp.components.AppScaffold
import uk.co.goingproprogramming.tbp.printer.IPrinterZebra
import uk.co.goingproprogramming.tbp.ui.theme.TestingBluetoothPrinterTheme
import java.io.File

@Composable
fun ScreenZebra(
    viewModel: ZebraViewModel = hiltViewModel(),
) {
    viewModel.state.observeAsState(initial = ZebraViewModel.State()).value.apply {
        ScreenZebraUI(
            state = this,
            onEvent = viewModel::onEvent,
        )
    }
}

private fun getBitmapFromImage(context: Context, drawable: Int): Bitmap =
    BitmapFactory.decodeResource(context.resources, drawable)

private fun saveDrawableAsBitmapOnFile(context: Context, drawable: Int): File {
    val bitmap = getBitmapFromImage(context, drawable)
    val file = File(context.cacheDir, "image.png")
    if (file.exists())
        file.delete()
    file.outputStream().use { outputStream ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
    }
    return file
}

@Composable
private fun ScreenZebraUI(
    state: ZebraViewModel.State,
    onEvent: (ZebraViewModel.Event) -> Unit,
) {
    BackHandler {
        onEvent(ZebraViewModel.Event.OnBack)
    }

    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        val file = saveDrawableAsBitmapOnFile(
            context = context,
            drawable = R.drawable.logo_android,
        )
        onEvent(ZebraViewModel.Event.OnBitmapFileChange(file))
    }

    if (state.printing) {
        AppDialogPrinting()
    }

    if (state.errorPrinting) {
        AppDialogError(
            message = stringResource(id = R.string.zebra_error_printing),
            onDismiss = { onEvent(ZebraViewModel.Event.OnErrorPrintingDismiss) },
        )
    }

    AppScaffold(
        title = state.printerName,
        onBack = { onEvent(ZebraViewModel.Event.OnBack) },
    ) {
        ZebraInches(
            zebraInchesSelected = state.zebraInches,
            onSelected = { onEvent(ZebraViewModel.Event.OnSelectedZebraInches(it)) },
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = state.textToPrint,
            onValueChange = { onEvent(ZebraViewModel.Event.OnTextToPrintChange(it)) },
            label = {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = stringResource(id = R.string.zebra_textToPrint_hint),
                )
            },
        )
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            enabled = state.textToPrint.trim().isNotBlank(),
            onClick = { onEvent(ZebraViewModel.Event.OnPrintText) },
        ) {
            Text(
                text = stringResource(id = R.string.zebra_textToPrint_button),
            )
        }
        state.bitmapFile?.let { bitmapFile ->
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Image(
                    modifier = Modifier
                        .size(200.dp)
                        .aspectRatio(1f)
                        .background(Color.Gray),
                    bitmap = BitmapFactory.decodeFile(bitmapFile.absolutePath)
                        .asImageBitmap(),
                    //painter = painterResource(id = R.drawable.logo_android),
                    contentDescription = stringResource(id = R.string.zebra_image_content),
                )
            }
        }
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            enabled = state.bitmapFile != null,
            onClick = { onEvent(ZebraViewModel.Event.OnPrintImage) },
        ) {
            Text(
                text = stringResource(id = R.string.zebra_imageToPrint_button),
            )
        }
    }
}

@Composable
private fun ZebraInches(
    zebraInchesSelected: IPrinterZebra.ZebraInches,
    onSelected: (IPrinterZebra.ZebraInches) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        AppRadioButton(
            text = stringResource(id = R.string.zebra_rw220),
            selected = zebraInchesSelected == IPrinterZebra.ZebraInches.Inches2,
            onSelected = { onSelected(IPrinterZebra.ZebraInches.Inches2) },
        )
        AppRadioButton(
            text = stringResource(id = R.string.zebra_rw320),
            selected = zebraInchesSelected == IPrinterZebra.ZebraInches.Inches3,
            onSelected = { onSelected(IPrinterZebra.ZebraInches.Inches3) },
        )
        AppRadioButton(
            text = stringResource(id = R.string.zebra_rw420),
            selected = zebraInchesSelected == IPrinterZebra.ZebraInches.Inches4,
            onSelected = { onSelected(IPrinterZebra.ZebraInches.Inches4) },
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_6A)
@Preview(showBackground = true, device = Devices.PIXEL_2)
@Composable
private fun ScreenZebraUIPreview() {
    TestingBluetoothPrinterTheme {
        ScreenZebraUI(
            state = ZebraViewModel.State(
                printerName = "Printer Name",
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_6A)
@Preview(showBackground = true, device = Devices.PIXEL_2)
@Composable
private fun ScreenZebraUIValuesPreview() {
    TestingBluetoothPrinterTheme {
        ScreenZebraUI(
            state = ZebraViewModel.State(
                printerName = "Printer Name",
                textToPrint = "This is a test",
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_2)
@Composable
private fun ScreenZebraUIPrintingPreview() {
    TestingBluetoothPrinterTheme {
        ScreenZebraUI(
            state = ZebraViewModel.State(
                printerName = "Printer Name",
                printing = true,
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_2)
@Composable
private fun ScreenZebraUIErrorPreview() {
    TestingBluetoothPrinterTheme {
        ScreenZebraUI(
            state = ZebraViewModel.State(
                printerName = "Printer Name",
                errorPrinting = true,
            ),
            onEvent = {},
        )
    }
}