package uk.co.goingproprogramming.tbp.printer

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Build
import com.zebra.sdk.printer.discovery.BluetoothDiscoverer
import com.zebra.sdk.printer.discovery.DiscoveredPrinter
import com.zebra.sdk.printer.discovery.DiscoveryHandler
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.CancellationException
import javax.inject.Inject

class PrinterBluetoothException(message: String) : Exception(message)

interface IPrinterBluetooth {
    data class BluetoothDiscovered(
        val name: String,
        val macAddress: String,
    )

    suspend fun isAvailable(): Boolean

    @Throws(PrinterBluetoothException::class)
    fun discover(): Flow<BluetoothDiscovered>
}

class PrinterBluetooth @Inject constructor(
    private val context: Context,
) : IPrinterBluetooth {
    companion object {
        fun getBluetoothAdapter(context: Context): BluetoothAdapter? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val bluetoothManager: BluetoothManager =
                    context.getSystemService(BluetoothManager::class.java)
                bluetoothManager.adapter
            } else {
                BluetoothAdapter.getDefaultAdapter()
            }
    }

    override suspend fun isAvailable(): Boolean =
        getBluetoothAdapter(context) != null

    override fun discover(): Flow<IPrinterBluetooth.BluetoothDiscovered> =
        try {
            callbackFlow {
                val callback = object : DiscoveryHandler {
                    override fun foundPrinter(discoveredPrinter: DiscoveredPrinter?) {
                        discoveredPrinter.toBluetoothDiscovered()?.let { bluetoothDiscovered ->
                            trySend(bluetoothDiscovered)
                        }
                    }

                    override fun discoveryFinished() {
                        channel.close()
                    }

                    override fun discoveryError(message: String?) {
                        cancel(CancellationException(message ?: "Error while discovering printers"))
                    }
                }
                BluetoothDiscoverer.findPrinters(context, callback)
                awaitClose {}
            }
        } catch (e: Exception) {
            throw PrinterBluetoothException(e.message ?: "Error while discovering")
        }
}

private fun DiscoveredPrinter?.toBluetoothDiscovered(): IPrinterBluetooth.BluetoothDiscovered? =
    this?.let {
        val map = this.discoveryDataMap
        map["FRIENDLY_NAME"]?.let { friendlyName ->
            map["MAC_ADDRESS"]?.let { macAddress ->
                IPrinterBluetooth.BluetoothDiscovered(
                    friendlyName,
                    macAddress
                )
            }
        }
    }