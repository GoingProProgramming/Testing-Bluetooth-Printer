package uk.co.goingproprogramming.tbp.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.co.goingproprogramming.tbp.ui.theme.TestingBluetoothPrinterTheme

@Composable
fun AppRadioButton(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onSelected: () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = onSelected,
        )
        Text(
            text = text,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppRadioButtonPreview() {
    TestingBluetoothPrinterTheme {
        AppRadioButton(
            text = "Zebra 2\"",
            selected = true,
            onSelected = {},
        )
    }
}