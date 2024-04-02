package uk.co.goingproprogramming.tbp.screens.bixolon

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.co.goingproprogramming.tbp.extensions.toFile
import uk.co.goingproprogramming.tbp.printer.IPrinterBixolon
import uk.co.goingproprogramming.tbp.printer.PrinterBixolonException
import uk.co.goingproprogramming.tbp.printer.PrinterBixolonExceptionType
import uk.co.goingproprogramming.tbp.screens.ViewModelBase
import uk.co.goingproprogramming.tbp.services.IServiceLogError
import uk.co.goingproprogramming.tbp.services.IServiceNavigation
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class BixolonViewModel @Inject constructor(
    private val serviceNavigation: IServiceNavigation,
    private val serviceLogError: IServiceLogError,
    private val printerBixolon: IPrinterBixolon,
) : ViewModelBase<BixolonViewModel.State>(State()) {
    data class State(
        val printing: Boolean = false,
        val printerName: String = "",
        val bitmapFile: File? = null,
        val errorType: PrinterBixolonExceptionType? = null,
    )

    sealed interface Event {
        data class OnBitmapFileChange(val bitmapFile: File) : Event
        data class OnBitmapUriChange(val context: Context, val uri: Uri) : Event
        data object OnPrintImage : Event
        data object OnBack : Event
        data object OnErrorPrintingDismiss : Event
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.OnBitmapFileChange -> doBitmapFileChange(event.bitmapFile)
            is Event.OnBitmapUriChange -> doBitmapUriChange(event.context, event.uri)
            is Event.OnBack -> doBack()
            Event.OnPrintImage -> doPrintImage()
            Event.OnErrorPrintingDismiss -> doErrorPrintingDismiss()
        }
    }

    init {
        localState = localState.copy(
            printerName = serviceNavigation.bluetoothDiscovered.name,
        )
    }

    private fun doBitmapFileChange(bitmapFile: File) {
        localState = localState.copy(
            bitmapFile = bitmapFile,
        )
    }

    private fun doBitmapUriChange(context: Context, uri: Uri) {
        localState.bitmapFile?.delete()

        localState = localState.copy(
            bitmapFile = uri.toFile(context, UUID.randomUUID().toString())
        )
    }

    private fun doPrintImage() {
        if (localState.bitmapFile == null) {
            return
        }

        print {
            printerBixolon.print(
                bitmapFile = localState.bitmapFile!!,
                bluetoothDiscovered = serviceNavigation.bluetoothDiscovered,
            )
        }
    }

    private fun doBack() {
        serviceNavigation.popBack()
    }

    private fun doErrorPrintingDismiss() {
        localState = localState.copy(
            errorType = null,
        )
    }

    private fun print(content: suspend () -> Unit) {
        localState = localState.copy(
            printing = true,
        )

        viewModelScope.launch(Dispatchers.IO) {
            try {
                content()
                viewModelScope.launch(Dispatchers.Main) {
                    localState = localState.copy(
                        printing = false,
                        errorType = null,
                    )
                }
            } catch (e: PrinterBixolonException) {
                serviceLogError.logError(e)
                viewModelScope.launch(Dispatchers.Main) {
                    localState = localState.copy(
                        printing = false,
                        errorType = e.type,
                    )
                }
            }
        }
    }
}