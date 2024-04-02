package uk.co.goingproprogramming.tbp.screens.brother

import android.content.Context
import android.os.ParcelFileDescriptor
import android.util.DisplayMetrics
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import uk.co.goingproprogramming.tbp.R
import uk.co.goingproprogramming.tbp.components.AppDialogError
import uk.co.goingproprogramming.tbp.components.AppDialogPrinting
import uk.co.goingproprogramming.tbp.components.AppScaffold
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
//        Text(
//            modifier = Modifier
//                .fillMaxWidth(),
//            text = stringResource(id = R.string.brother_pdfToPrint_tapToChange),
//            textAlign = TextAlign.Center,
//            fontStyle = FontStyle.Italic,
//        )
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
        state.pdfFile?.let {
            PDFReader(
//                modifier = Modifier
//                    .clickable { launcher.launch("application/pdf") },
                file = it,
            )
        }
    }
}

// https://medium.com/telepass-digital/how-to-show-a-pdf-with-jetpack-compose-74fc773adbd0
@Composable
fun PDFReader(
    modifier: Modifier = Modifier,
    file: File,
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        val pdfRender = PdfRender(
            fileDescriptor = ParcelFileDescriptor.open(
                file,
                ParcelFileDescriptor.MODE_READ_ONLY
            )
        )

        DisposableEffect(key1 = Unit) {
            onDispose {
                pdfRender.close()
            }
        }
        LazyColumn {
            items(count = pdfRender.pageCount) { index ->
                BoxWithConstraints(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val page = pdfRender.pageLists[index]
                    DisposableEffect(key1 = Unit) {
                        page.load()
                        onDispose {
                            page.recycle()
                        }
                    }
                    page.pageContent.collectAsState().value?.asImageBitmap()?.let {
                        Image(
                            bitmap = it,
                            contentDescription = "Pdf page number: $index",
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.FillWidth
                        )
                    } ?: run {
                        val context = LocalContext.current
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(
                                    page
                                        .heightByWidth(constraints.maxWidth)
                                        .pxToDp(context)
                                )
                        )
                    }
                }
            }
        }
    }
}

fun Int.pxToDp(context: Context): Dp =
    Dp(this / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT))

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