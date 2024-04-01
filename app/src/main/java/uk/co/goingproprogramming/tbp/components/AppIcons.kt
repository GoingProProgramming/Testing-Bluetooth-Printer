package uk.co.goingproprogramming.tbp.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.co.goingproprogramming.tbp.R
import uk.co.goingproprogramming.tbp.ui.theme.TestingBluetoothPrinterTheme

@Composable
fun AppIconBack(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary,
) {
    Icon(
        modifier = modifier,
        imageVector = Icons.Rounded.ArrowBackIosNew,
        contentDescription = stringResource(id = R.string.icon_back),
        tint = tint,
    )
}

@Composable
fun AppIconNext(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary,
) {
    Icon(
        modifier = modifier,
        imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
        contentDescription = stringResource(id = R.string.icon_next),
        tint = tint,
    )
}

@Preview(showBackground = true)
@Composable
private fun AppIconsPreview() {
    TestingBluetoothPrinterTheme {
        Row(
            modifier = Modifier
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AppIconBack()
            AppIconNext()
        }
    }
}