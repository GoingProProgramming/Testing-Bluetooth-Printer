package uk.co.goingproprogramming.tbp.screens.zebra

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.co.goingproprogramming.tbp.extensions.toFile
import uk.co.goingproprogramming.tbp.printer.IPrinterZebra
import uk.co.goingproprogramming.tbp.screens.ViewModelBase
import uk.co.goingproprogramming.tbp.services.IServiceLogError
import uk.co.goingproprogramming.tbp.services.IServiceNavigation
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ZebraViewModel @Inject constructor(
    private val serviceNavigation: IServiceNavigation,
    private val serviceLogError: IServiceLogError,
    private val printerZebra: IPrinterZebra,
) : ViewModelBase<ZebraViewModel.State>(State()) {
    data class State(
        val printing: Boolean = false,
        val printerName: String = "",
        val zebraInches: IPrinterZebra.ZebraInches = IPrinterZebra.ZebraInches.Inches2,
        val textToPrint: String = "",
        val bitmapFile: File? = null,
        val storingImage: Boolean = false,
        val storedImageName: String = "",
        val errorPrinting: Boolean = false,
    )

    sealed interface Event {
        data class OnSelectedZebraInches(val zebraInches: IPrinterZebra.ZebraInches) : Event
        data class OnTextToPrintChange(val text: String) : Event
        data object OnPrintText : Event
        data class OnBitmapFileChange(val bitmapFile: File) : Event
        data class OnBitmapUriChange(val context: Context, val uri: Uri) : Event
        data object OnPrintImage : Event
        data class OnPrintStoredImageTextChange(val storedImageName: String) : Event
        data object OnStoreImage : Event
        data object OnPrintStoredImage : Event
        data object OnBack : Event
        data object OnErrorPrintingDismiss : Event
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.OnSelectedZebraInches -> doSelectedZebraInches(event.zebraInches)
            is Event.OnTextToPrintChange -> doTextToPrintChange(event.text)
            Event.OnPrintText -> doPrintText()
            is Event.OnBitmapFileChange -> doBitmapFileChange(event.bitmapFile)
            is Event.OnBitmapUriChange -> doBitmapUriChange(event.context, event.uri)
            Event.OnPrintImage -> doPrintImage()
            is Event.OnPrintStoredImageTextChange -> doPrintStoredImageTextChange(event.storedImageName)
            Event.OnStoreImage -> doStoreImage()
            Event.OnPrintStoredImage -> doPrintStoredImage()
            is Event.OnBack -> doBack()
            Event.OnErrorPrintingDismiss -> doErrorPrintingDismiss()
        }
    }

    init {
        localState = localState.copy(
            printerName = serviceNavigation.bluetoothDiscovered.name,
        )
    }

    private fun doSelectedZebraInches(zebraInches: IPrinterZebra.ZebraInches) {
        localState = localState.copy(
            zebraInches = zebraInches,
        )
    }

    private fun doTextToPrintChange(text: String) {
        localState = localState.copy(
            textToPrint = text,
        )
    }

    private fun doPrintText() {
        if (localState.textToPrint.isBlank()) {
            return
        }

        print {
            printerZebra.print(
                text = localState.textToPrint,
                macAddress = serviceNavigation.bluetoothDiscovered.macAddress,
                zebraInches = localState.zebraInches,
            )
        }
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
            printerZebra.print(
                bitmapFile = localState.bitmapFile!!,
                macAddress = serviceNavigation.bluetoothDiscovered.macAddress,
                zebraInches = localState.zebraInches,
            )
        }
    }

    private fun doPrintStoredImageTextChange(storedImageName: String) {
        localState = localState.copy(
            storedImageName = storedImageName,
        )
    }

    private fun doStoreImage() {
        if (localState.storedImageName.isBlank()) {
            return
        }
        if (localState.bitmapFile == null) {
            return
        }

        storeImage {
            printerZebra.storeImage(
                imageName = localState.storedImageName,
                bitmapFile = localState.bitmapFile!!,
                macAddress = serviceNavigation.bluetoothDiscovered.macAddress,
            )
        }
    }

    private fun doPrintStoredImage() {
        if (localState.storedImageName.isBlank()) {
            return
        }

        print {
            printerZebra.printStoredImage(
                imageName = localState.storedImageName,
                macAddress = serviceNavigation.bluetoothDiscovered.macAddress,
                zebraInches = localState.zebraInches,
            )
        }
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

    private fun storeImage(content: suspend () -> Unit) {
        localState = localState.copy(
            storingImage = true,
        )

        viewModelScope.launch(Dispatchers.IO) {
            try {
                content()
                viewModelScope.launch(Dispatchers.Main) {
                    localState = localState.copy(
                        storingImage = false,
                        errorPrinting = false,
                    )
                }
            } catch (e: Exception) {
                serviceLogError.logError(e)
                viewModelScope.launch(Dispatchers.Main) {
                    localState = localState.copy(
                        storingImage = false,
                        errorPrinting = true,
                    )
                }
            }
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
}