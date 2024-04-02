package uk.co.goingproprogramming.tbp.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import uk.co.goingproprogramming.tbp.R
import uk.co.goingproprogramming.tbp.ui.theme.TestingBluetoothPrinterTheme

@Composable
fun AppDialogDiscovering() {
    AppDialog(
        title = stringResource(id = R.string.dialog_discovering_title),
        message = stringResource(id = R.string.dialog_discovering_message),
    )
}

@Composable
fun AppDialogPrinting() {
    AppDialog(
        title = stringResource(id = R.string.dialog_printing_title),
        message = stringResource(id = R.string.dialog_printing_message),
    )
}

@Composable
fun AppDialogStoringImage() {
    AppDialog(
        title = stringResource(id = R.string.dialog_storingImage_title),
        message = stringResource(id = R.string.dialog_storingImage_message),
    )
}

@Composable
fun AppDialogLoading() {
    AppDialog(
        title = stringResource(id = R.string.dialog_loading_title),
        message = stringResource(id = R.string.dialog_loading_message),
    )
}

@Composable
fun AppDialogError(
    message: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text(
                    text = stringResource(id = R.string.dialog_button_ok),
                )
            }
        },
        title = {
            Text(
                text = stringResource(id = R.string.dialog_error_title),
            )
        },
        text = {
            Text(
                text = message,
            )
        }
    )
}

@Composable
private fun AppDialog(
    title: String,
    message: String,
) {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {},
        title = {
            Text(
                text = title,
            )
        },
        text = {
            Text(
                text = message,
            )
        }
    )
}

@Preview
@Composable
private fun AppDialogDiscoveringPreview() {
    TestingBluetoothPrinterTheme {
        AppDialogDiscovering()
    }
}

@Preview
@Composable
private fun AppDialogPrintingPreview() {
    TestingBluetoothPrinterTheme {
        AppDialogPrinting()
    }
}

@Preview
@Composable
private fun AppDialogStoringImagePreview() {
    TestingBluetoothPrinterTheme {
        AppDialogStoringImage()
    }
}

@Preview
@Composable
private fun AppDialogLoadingPreview() {
    TestingBluetoothPrinterTheme {
        AppDialogLoading()
    }
}

@Preview
@Composable
private fun AppDialogErrorPreview() {
    TestingBluetoothPrinterTheme {
        AppDialogError(
            message = "This is the error message",
            onDismiss = {},
        )
    }
}