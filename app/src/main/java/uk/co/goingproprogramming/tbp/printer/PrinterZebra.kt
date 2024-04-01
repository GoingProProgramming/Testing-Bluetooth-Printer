package uk.co.goingproprogramming.tbp.printer

import com.zebra.sdk.comm.BluetoothConnection
import com.zebra.sdk.graphics.internal.ZebraImageAndroid
import com.zebra.sdk.printer.PrinterLanguage
import com.zebra.sdk.printer.ZebraPrinterFactory
import kotlinx.coroutines.delay
import java.io.File
import javax.inject.Inject

class PrinterZebraException(message: String) : Exception(message)

interface IPrinterZebra {
    enum class ZebraInches {
        Inches2, Inches3, Inches4
    }

    fun isZebraPrinter(name: String): Boolean

    @Throws(PrinterZebraException::class)
    suspend fun storeImage(imageName: String, bitmapFile: File, macAddress: String)

    @Throws(PrinterZebraException::class)
    suspend fun print(text: String, macAddress: String, zebraInches: ZebraInches)

    @Throws(PrinterZebraException::class)
    suspend fun print(bitmapFile: File, macAddress: String, zebraInches: ZebraInches)
}

class PrinterZebra @Inject constructor() : IPrinterZebra {
    override fun isZebraPrinter(name: String): Boolean =
        name.startsWith("XXXX", ignoreCase = true)

    override suspend fun storeImage(
        imageName: String,
        bitmapFile: File,
        macAddress: String,
    ) =
        try {
            print(macAddress) { connection ->
                val bitmap = bitmapFile.toBitmap()
                ZebraPrinterFactory.getInstance(PrinterLanguage.CPCL, connection).storeImage(
                    imageName, ZebraImageAndroid(bitmap), bitmap.width, bitmap.height
                )
            }
        } catch (e: Exception) {
            throw PrinterZebraException(e.message ?: "Error while storing image")
        }

    override suspend fun print(
        text: String,
        macAddress: String,
        zebraInches: IPrinterZebra.ZebraInches,
    ) =
        print(macAddress) { connection ->
            val printerBuffer = PrinterBuffer(text, 500)
            var line = printerBuffer.getNextData()
            while (line != null) {
                connection.write(line.toByteArray())
                delay(750)
                line = printerBuffer.getNextData()
            }
        }

    override suspend fun print(
        bitmapFile: File,
        macAddress: String,
        zebraInches: IPrinterZebra.ZebraInches,
    ) =
        print(macAddress) { connection ->
            val bitmap = bitmapFile.toBitmap()
            ZebraPrinterFactory.getInstance(PrinterLanguage.CPCL, connection).let { zebraPrinter ->
                connection.write(resetPrinter(zebraInches).toByteArray())
                connection.write(commandBitmap.toByteArray())
                zebraPrinter.printImage(
                    ZebraImageAndroid(bitmap), 0, 0, bitmap.width, bitmap.height, false
                )
            }
        }

    data class PrinterBuffer(
        val text: String,
        val bufferSize: Int,
    ) {
        private var textToPrint: String = text

        fun getNextData(): String? {
            val line: String
            return if (textToPrint.isNotEmpty()) {
                if (textToPrint.length > bufferSize) {
                    var count = bufferSize
                    for (i in count downTo 0) {
                        if (textToPrint[i] == '\n') {
                            count = i
                            break
                        }
                    }
                    line = textToPrint.substring(0, count)
                    textToPrint = textToPrint.substring(count)
                } else {
                    line = textToPrint
                    textToPrint = ""
                }
                line
            } else {
                null
            }
        }
    }

    private fun resetPrinter(zebraInches: IPrinterZebra.ZebraInches): String {
        val pageWidth = when (zebraInches) {
            IPrinterZebra.ZebraInches.Inches2 -> 380
            IPrinterZebra.ZebraInches.Inches3 -> 580
            IPrinterZebra.ZebraInches.Inches4 -> 780
        }
        return commandReset.format(pageWidth)
    }

    private suspend fun print(macAddress: String, content: suspend (BluetoothConnection) -> Unit) =
        try {
            BluetoothConnection(macAddress).let { connection ->
                connection.open()
                try {
                    content(connection)
                } finally {
                    connection.close()
                }
            }
        } catch (e: Exception) {
            throw PrinterZebraException(e.message ?: "Error while printing")
        }

    private val commandReset =
        "! U1\r\n! U1 SETLP-TIMEOUT 24\r\n! U1 COUNTRY ITALY\r\n! U1 LMARGIN 20\r\n! U1 PW {0}\r\n"
    private val commandBitmap =
        "! U1 JOURNAL\r\n! U1 SETFF 50 2\r\n"
}