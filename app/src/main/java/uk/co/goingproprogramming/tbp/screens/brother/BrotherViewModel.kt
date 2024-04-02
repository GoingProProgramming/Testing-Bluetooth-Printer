package uk.co.goingproprogramming.tbp.screens.brother

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uk.co.goingproprogramming.tbp.extensions.toFile
import uk.co.goingproprogramming.tbp.printer.IPrinterBrother
import uk.co.goingproprogramming.tbp.screens.ViewModelBase
import uk.co.goingproprogramming.tbp.services.IServiceLogError
import uk.co.goingproprogramming.tbp.services.IServiceNavigation
import java.io.File
import java.io.InputStream
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class BrotherViewModel @Inject constructor(
    private val serviceNavigation: IServiceNavigation,
    private val serviceLogError: IServiceLogError,
    private val printerBrother: IPrinterBrother,
) : ViewModelBase<BrotherViewModel.State>(State()) {
    data class State(
        val printing: Boolean = false,
        val loading: Boolean = false,
        val printerName: String = "",
        val pdfFile: File? = null,
        val pdfInputStream: InputStream? = null,
        val errorPrinting: Boolean = false,
    )

    sealed interface Event {
        data class OnPdfFileChange(val pdfFile: File) : Event
        data class OnPdfUriChange(val context: Context, val uri: Uri) : Event
        data object OnPrintPdf : Event
        data object OnBack : Event
        data object OnErrorPrintingDismiss : Event
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.OnPdfFileChange -> doPdfFileChange(event.pdfFile)
            is Event.OnPdfUriChange -> doPdfUriChange(event.context, event.uri)
            Event.OnPrintPdf -> doPrintPdf()
            is Event.OnBack -> doBack()
            Event.OnErrorPrintingDismiss -> doErrorPrintingDismiss()
        }
    }

    init {
        localState = localState.copy(
            printerName = serviceNavigation.bluetoothDiscovered.name,
        )
    }

    private fun doPdfFileChange(pdfFile: File) {
        localState = localState.copy(
            pdfFile = pdfFile,
            pdfInputStream = pdfFile.inputStream(),
        )
    }

    private fun doPdfUriChange(context: Context, uri: Uri) {
        localState.pdfFile?.delete()
        localState.pdfInputStream?.close()
        localState = localState.copy(
            pdfInputStream = null,
            loading = true,
        )

        viewModelScope.launch(Dispatchers.IO) {
            delay(1000)
            viewModelScope.launch(Dispatchers.Main) {
                val pdfFile = uri.toFile(context, UUID.randomUUID().toString())
                localState = localState.copy(
                    pdfFile = pdfFile,
                    pdfInputStream = pdfFile.inputStream(),
                    loading = false,
                )
            }
        }
    }

    private fun doPrintPdf() {
        if (localState.pdfFile == null) {
            return
        }

        print {
            printerBrother.print(
                pdfFile = localState.pdfFile!!,
                bluetoothDiscovered = serviceNavigation.bluetoothDiscovered,
            )
        }
    }

    private fun doBack() {
        serviceNavigation.popBack()
    }

    private fun doErrorPrintingDismiss() {
        localState = localState.copy(
            errorPrinting = false,
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
                        errorPrinting = false,
                    )
                }
            } catch (e: Exception) {
                serviceLogError.logError(e)
                viewModelScope.launch(Dispatchers.Main) {
                    localState = localState.copy(
                        printing = false,
                        errorPrinting = true,
                    )
                }
            }
        }
    }
}