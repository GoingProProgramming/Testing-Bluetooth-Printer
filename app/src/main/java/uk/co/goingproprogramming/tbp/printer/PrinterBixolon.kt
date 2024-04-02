package uk.co.goingproprogramming.tbp.printer

import android.content.Context
import com.bxl.config.editor.BXLConfigLoader
import jpos.POSPrinter
import jpos.POSPrinterConst
import jpos.config.JposEntry
import kotlinx.coroutines.delay
import java.io.File
import java.nio.ByteBuffer
import javax.inject.Inject

enum class PrinterBixolonExceptionType {
    UnsupportedPrinter, DeviceNotEnabled, PrintingError,
}

data class PrinterBixolonException(val type: PrinterBixolonExceptionType) : Exception()

interface IPrinterBixolon {
    fun isBixolonPrinter(name: String): Boolean

    @Throws(PrinterBixolonException::class)
    suspend fun print(
        bitmapFile: File,
        bluetoothDiscovered: IPrinterBluetooth.BluetoothDiscovered,
    )
}

class PrinterBixolon @Inject constructor(
    private val context: Context,
) : IPrinterBixolon {
    override fun isBixolonPrinter(name: String): Boolean =
        getProductName(name) != null

    override suspend fun print(
        bitmapFile: File,
        bluetoothDiscovered: IPrinterBluetooth.BluetoothDiscovered,
    ) {
        val productName = getProductName(bluetoothDiscovered.name)
            ?: throw PrinterBixolonException(PrinterBixolonExceptionType.UnsupportedPrinter)

        try {
            val posPrinter = POSPrinter(context)
            val bxlConfigLoader = BXLConfigLoader(context)
            try {
                bxlConfigLoader.openFile()
            } catch (_: Throwable) {
                bxlConfigLoader.newFile()
            }

            for (entry in bxlConfigLoader.entries) {
                val jposEntry = entry as JposEntry
                if (jposEntry.logicalName == bluetoothDiscovered.name) {
                    bxlConfigLoader.removeEntry(jposEntry.logicalName)
                }
            }
            bxlConfigLoader.addEntry(
                bluetoothDiscovered.name,
                BXLConfigLoader.DEVICE_CATEGORY_POS_PRINTER,
                productName,
                0,
                bluetoothDiscovered.macAddress
            )
            bxlConfigLoader.saveFile()

            posPrinter.open(bluetoothDiscovered.name)
            try {
                posPrinter.claim(5000 * 2)
                posPrinter.deviceEnabled = true
                posPrinter.asyncMode = false

                // Printing
                if (!posPrinter.deviceEnabled)
                    throw PrinterBixolonException(PrinterBixolonExceptionType.DeviceNotEnabled)

                val buffer = ByteBuffer.allocate(4)
                buffer.put(POSPrinterConst.PTR_S_RECEIPT.toByte())
                buffer.put(0.toByte()) // brightness
                buffer.put(0.toByte()) // compress
                buffer.put(0.toByte()) // dither

                val bitmap = bitmapFile.toBitmap()
                posPrinter.printBitmap(
                    buffer.getInt(0),
                    bitmap,
                    bitmap.width,
                    POSPrinterConst.PTR_BM_LEFT
                )

                delay(500)
            } finally {
                posPrinter.close()
            }
        } catch (e: PrinterBrotherException) {
            throw e
        } catch (e: Exception) {
            throw PrinterBixolonException(PrinterBixolonExceptionType.PrintingError)
        }
    }

    private fun getProductName(name: String): String? =
        when (name) {
            "SPP-R200II" -> BXLConfigLoader.PRODUCT_NAME_SPP_R200II
            "SPP-R200III" -> BXLConfigLoader.PRODUCT_NAME_SPP_R200III
            "SPP-R210" -> BXLConfigLoader.PRODUCT_NAME_SPP_R210
            "SPP-R215" -> BXLConfigLoader.PRODUCT_NAME_SPP_R215
            "SPP-R220" -> BXLConfigLoader.PRODUCT_NAME_SPP_R220
            "SPP-R300" -> BXLConfigLoader.PRODUCT_NAME_SPP_R300
            "SPP-R310" -> BXLConfigLoader.PRODUCT_NAME_SPP_R310
            "SPP-R318" -> BXLConfigLoader.PRODUCT_NAME_SPP_R318
            "SPP-R400" -> BXLConfigLoader.PRODUCT_NAME_SPP_R400
            "SPP-R410" -> BXLConfigLoader.PRODUCT_NAME_SPP_R410
            "SPP-R418" -> BXLConfigLoader.PRODUCT_NAME_SPP_R418
            "SPP-100II" -> BXLConfigLoader.PRODUCT_NAME_SPP_100II
            "SRP-350IIOBE" -> BXLConfigLoader.PRODUCT_NAME_SRP_350IIOBE
            "SRP-350III" -> BXLConfigLoader.PRODUCT_NAME_SRP_350III
            "SRP-352III" -> BXLConfigLoader.PRODUCT_NAME_SRP_352III
            "SRP-350plusIII" -> BXLConfigLoader.PRODUCT_NAME_SRP_350PLUSIII
            "SRP-352plusIII" -> BXLConfigLoader.PRODUCT_NAME_SRP_352PLUSIII
            "SRP-380" -> BXLConfigLoader.PRODUCT_NAME_SRP_380
            "SRP-382" -> BXLConfigLoader.PRODUCT_NAME_SRP_382
            "SRP-383" -> BXLConfigLoader.PRODUCT_NAME_SRP_383
            "SRP-340II" -> BXLConfigLoader.PRODUCT_NAME_SRP_340II
            "SRP-342II" -> BXLConfigLoader.PRODUCT_NAME_SRP_342II
            "SRP-Q200" -> BXLConfigLoader.PRODUCT_NAME_SRP_Q200
            "SRP-Q300" -> BXLConfigLoader.PRODUCT_NAME_SRP_Q300
            "SRP-Q302" -> BXLConfigLoader.PRODUCT_NAME_SRP_Q302
            "SRP-QE300" -> BXLConfigLoader.PRODUCT_NAME_SRP_QE300
            "SRP-QE302" -> BXLConfigLoader.PRODUCT_NAME_SRP_QE302
            "SRP-E300" -> BXLConfigLoader.PRODUCT_NAME_SRP_E300
            "SRP-E302" -> BXLConfigLoader.PRODUCT_NAME_SRP_E302
            "SRP-B300" -> BXLConfigLoader.PRODUCT_NAME_SRP_B300
            "SRP-330II" -> BXLConfigLoader.PRODUCT_NAME_SRP_330II
            "SRP-332II" -> BXLConfigLoader.PRODUCT_NAME_SRP_332II
            "SRP-S200" -> BXLConfigLoader.PRODUCT_NAME_SRP_S200
            "SRP-S300" -> BXLConfigLoader.PRODUCT_NAME_SRP_S300
            "SRP-S320" -> BXLConfigLoader.PRODUCT_NAME_SRP_S320
            "SRP-S3000" -> BXLConfigLoader.PRODUCT_NAME_SRP_S3000
            "SRP-F310" -> BXLConfigLoader.PRODUCT_NAME_SRP_F310
            "SRP-F312" -> BXLConfigLoader.PRODUCT_NAME_SRP_F312
            "SRP-F310II" -> BXLConfigLoader.PRODUCT_NAME_SRP_F310II
            "SRP-F312II" -> BXLConfigLoader.PRODUCT_NAME_SRP_F312II
            "SRP-F313II" -> BXLConfigLoader.PRODUCT_NAME_SRP_F313II
            "SRP-275III" -> BXLConfigLoader.PRODUCT_NAME_SRP_275III
            "BK3-2" -> BXLConfigLoader.PRODUCT_NAME_BK3_2
            "BK3-3" -> BXLConfigLoader.PRODUCT_NAME_BK3_3
            "SLP X-Series" -> BXLConfigLoader.PRODUCT_NAME_SLP_X_SERIES
            "SLP-DX420" -> BXLConfigLoader.PRODUCT_NAME_SLP_DX420
            "SPP-L410II" -> BXLConfigLoader.PRODUCT_NAME_SPP_L410II
            "MSR" -> BXLConfigLoader.PRODUCT_NAME_MSR
            "CashDrawer" -> BXLConfigLoader.PRODUCT_NAME_CASH_DRAWER
            "LocalSmartCardRW" -> BXLConfigLoader.PRODUCT_NAME_LOCAL_SMART_CARD_RW
            "SmartCardRW" -> BXLConfigLoader.PRODUCT_NAME_SMART_CARD_RW
            else -> null
        }
}

/*
public class BixolonPrintAsyncTask extends AsyncTask<Void, Void, Void> {

    public interface IBixolonPrintAsyncTaskCallback {
        void onError(String errorMessage);
    }
	public BixolonPrintAsyncTask(Context context, Bitmap bipmapToPrint, GuiBluetoothPrinter guiBluetoothPrinter,
                                 IBixolonPrintAsyncTaskCallback bixolonPrintAsyncTaskCallback
		this.context = context;
		this.bipmapToPrint = bipmapToPrint;
		this.guiBluetoothPrinter = guiBluetoothPrinter;
        this.bixolonPrintAsyncTaskCallback = bixolonPrintAsyncTaskCallback;
	}

	@Override
	protected void onPostExecute(Void aVoid) {
        try {
            if (executionException != null) {
                if (executionException instanceof ConnectionException) {
					bixolonPrintAsyncTaskCallback.onError(context.getString(R.string.messagePrintCommunicationError));
                } else if (guiBluetoothPrinter != null && guiBluetoothPrinter.getMacAddress() == null) {
					bixolonPrintAsyncTaskCallback.onError(context.getString(R.string.messagePrintNotConfiguredError));
                } else {
                    ErrorManagement.logError(context, executionException);
                }
            }
        } catch (Exception ex) {
            ErrorManagement.logError(context, ex);
        }
	}

	@Override
	protected Void doInBackground(Void... params) {
		BixolonPrinter bixolonPrinter = null;
		try {
			Thread.currentThread().setName("BixolonPrintAsyncTask");
			bixolonPrinter = new BixolonPrinter(context);
			if (bixolonPrinter.printerOpen(0, guiBluetoothPrinter.getName(), guiBluetoothPrinter.getMacAddress(), true)) {
					/*
					//int cSet=bxlPrinter.getCharacterSet(); default CS_437_USA_STANDARD_EUROPE
					bxlPrinter.setCharacterSet(BXLConst.CS_852_LATIN2);
					bxlPrinter.printText("CS_852_LATIN2" + "\n", 1, 0, (0 + 9));
					bxlPrinter.setCharacterSet(BXLConst.CS_858_EURO);
					bxlPrinter.printText("CS_858_EURO" + "\n", 1, 0, (0 + 1));
					bxlPrinter.setCharacterSet(BXLConst.CS_1252_LATIN1);
					bxlPrinter.printText("CS_1252_LATIN1" + "\n", 1, 0, (0 + 1));
					*/
				if (UtilityBitmap.getEnableImageNotice(context) && bipmapToPrint!= null){	//stampa immagine strisciatina
					bixolonPrinter.printImage(bipmapToPrint, bipmapToPrint.getWidth(), 1, 0, 0, 0);
					//Lo sleep è fondamentale, altrimenti non stampa!!!
					Thread.sleep(300); // Don't strees the printer while printing the Bitmap... it don't like it.
				}else{	//text only
					String[] lines = textToPrint.split("\n");
					for (String line : lines) { //for each row
						bixolonPrinter.printText(line + "\n", 1, 0, (0 + 1));
					}
				}
				bixolonPrinter.printerClose();

			} else {
				//rtc = false;
			}


		} catch (Exception ex) {
            executionException = ex;
		}
		return null;
	}

	private Context context;
	private Bitmap bipmapToPrint;
	private String textToPrint;
	private GuiBluetoothPrinter guiBluetoothPrinter;
    private IBixolonPrintAsyncTaskCallback bixolonPrintAsyncTaskCallback;
    private Exception executionException;
}

 */