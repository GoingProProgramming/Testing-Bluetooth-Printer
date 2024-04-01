package uk.co.goingproprogramming.tbp.screens.zebra

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.co.goingproprogramming.tbp.printer.IPrinterZebra
import uk.co.goingproprogramming.tbp.screens.ViewModelBase
import uk.co.goingproprogramming.tbp.services.IServiceNavigation
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ZebraViewModel @Inject constructor(
    private val serviceNavigation: IServiceNavigation,
    private val printerZebra: IPrinterZebra,
) : ViewModelBase<ZebraViewModel.State>(State()) {
    data class State(
        val printing: Boolean = false,
        val errorPrinting: Boolean = false,
        val printerName: String = "",
        val zebraInches: IPrinterZebra.ZebraInches = IPrinterZebra.ZebraInches.Inches2,
        val textToPrint: String = "",
        val bitmapFile: File? = null,
    )

    sealed interface Event {
        data class OnSelectedZebraInches(val zebraInches: IPrinterZebra.ZebraInches) : Event
        data class OnTextToPrintChange(val text: String) : Event
        data object OnPrintText : Event
        data class OnBitmapFileChange(val bitmapFile: File) : Event
        data object OnPrintImage : Event
        data object OnBack : Event
        data object OnErrorPrintingDismiss : Event
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.OnSelectedZebraInches -> doSelectedZebraInches(event.zebraInches)
            is Event.OnTextToPrintChange -> doTextToPrintChange(event.text)
            Event.OnPrintText -> doPrintText()
            is Event.OnBitmapFileChange -> doBitmapFileChange(event.bitmapFile)
            Event.OnPrintImage -> doPrintImage()
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
        localState = localState.copy(
            printing = true,
        )

        viewModelScope.launch(Dispatchers.IO) {
            try {
                printerZebra.print(
                    text = localState.textToPrint,
                    macAddress = serviceNavigation.bluetoothDiscovered.macAddress,
                    zebraInches = localState.zebraInches,
                )
                viewModelScope.launch(Dispatchers.Main) {
                    localState = localState.copy(
                        printing = false,
                        errorPrinting = false,
                    )
                }
            } catch (_: Exception) {
                viewModelScope.launch(Dispatchers.Main) {
                    localState = localState.copy(
                        printing = false,
                        errorPrinting = true,
                    )
                }
            }
        }
    }

    private fun doBitmapFileChange(bitmapFile: File) {
        localState = localState.copy(
            bitmapFile = bitmapFile,
        )
    }

    private fun doPrintImage() {
        if (localState.bitmapFile == null) {
            return
        }

        localState = localState.copy(
            printing = true,
        )

        viewModelScope.launch(Dispatchers.IO) {
            try {
                printerZebra.print(
                    bitmapFile = localState.bitmapFile!!,
                    macAddress = serviceNavigation.bluetoothDiscovered.macAddress,
                    zebraInches = localState.zebraInches,
                )
                viewModelScope.launch(Dispatchers.Main) {
                    localState = localState.copy(
                        printing = false,
                        errorPrinting = false,
                    )
                }
            } catch (_: Exception) {
                viewModelScope.launch(Dispatchers.Main) {
                    localState = localState.copy(
                        printing = false,
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