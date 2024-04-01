package uk.co.goingproprogramming.tbp.printer

import android.content.Context
import com.brother.sdk.lmprinter.Channel
import com.brother.sdk.lmprinter.OpenChannelError
import com.brother.sdk.lmprinter.PrintError
import com.brother.sdk.lmprinter.PrinterDriver
import com.brother.sdk.lmprinter.PrinterDriverGenerator
import com.brother.sdk.lmprinter.PrinterModel
import com.brother.sdk.lmprinter.setting.QLPrintSettings
import java.io.File

enum class PrinterBrotherExceptionType {
    UnsupportedPrinter, ErrorConnecting, ErrorPrinting,
}

data class PrinterBrotherException(val type: PrinterBrotherExceptionType) : Exception()

interface IPrinterBrother {
    @Throws(PrinterBrotherException::class)
    suspend fun print(pdfFile: File, bluetoothDiscovered: IPrinterBluetooth.BluetoothDiscovered)
}

class PrinterBrother(
    private val context: Context,
) : IPrinterBrother {
    override suspend fun print(
        pdfFile: File,
        bluetoothDiscovered: IPrinterBluetooth.BluetoothDiscovered,
    ) {
        val printerModel = getPrinterModel(bluetoothDiscovered.name)
        val bluetoothAdapter = PrinterBluetooth.getBluetoothAdapter(context)
        val channel = Channel.newBluetoothChannel(bluetoothDiscovered.macAddress, bluetoothAdapter)
        val printerDriver = getDriver(channel)
        try {
            QLPrintSettings(printerModel).apply {
                workPath = pdfFile.parent
                val printError = printerDriver.printPDF(pdfFile.absolutePath, this)
                if (printError.code != PrintError.ErrorCode.NoError) {
                    throw PrinterBrotherException(PrinterBrotherExceptionType.ErrorPrinting)
                }
            }
        } finally {
            printerDriver.closeChannel()
        }
    }

    private fun getPrinterModel(name: String): PrinterModel =
        if (name.startsWith("PJ-762"))
            PrinterModel.PJ_762
        else if (name.startsWith("PJ-763"))
            PrinterModel.PJ_763
        else
            throw PrinterBrotherException(PrinterBrotherExceptionType.UnsupportedPrinter)

    private fun getDriver(channel: Channel): PrinterDriver =
        PrinterDriverGenerator.openChannel(channel).let {
            if (it.error.code != OpenChannelError.ErrorCode.NoError)
                throw PrinterBrotherException(PrinterBrotherExceptionType.ErrorConnecting)
            it.driver
        }
}