package uk.co.goingproprogramming.tbp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.co.goingproprogramming.tbp.printer.IPrinterBluetooth
import uk.co.goingproprogramming.tbp.ui.theme.TestingBluetoothPrinterTheme

data class UIBluetoothDiscovered(
    val bluetoothDiscovered: IPrinterBluetooth.BluetoothDiscovered,
    val enabled: Boolean,
)

@Composable
fun AppBluetoothDiscovered(
    uiBluetoothDiscovered: UIBluetoothDiscovered,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = if (uiBluetoothDiscovered.enabled) Color.White else Color.Gray)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
        ) {
            Text(
                text = uiBluetoothDiscovered.bluetoothDiscovered.name,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = uiBluetoothDiscovered.bluetoothDiscovered.macAddress,
            )
        }
        AppIconNext()
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_6A)
@Composable
private fun AppBluetoothDiscoveredEnabledPreview() {
    TestingBluetoothPrinterTheme {
        AppBluetoothDiscovered(
            uiBluetoothDiscovered = UIBluetoothDiscovered(
                bluetoothDiscovered = IPrinterBluetooth.BluetoothDiscovered(
                    name = "Printer",
                    macAddress = "00:00:00:00:00:00",
                ),
                enabled = true,
            ),
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_6A)
@Composable
private fun AppBluetoothDiscoveredPDisabledreview() {
    TestingBluetoothPrinterTheme {
        AppBluetoothDiscovered(
            uiBluetoothDiscovered = UIBluetoothDiscovered(
                bluetoothDiscovered = IPrinterBluetooth.BluetoothDiscovered(
                    name = "Printer",
                    macAddress = "00:00:00:00:00:00",
                ),
                enabled = false,
            ),
        )
    }
}