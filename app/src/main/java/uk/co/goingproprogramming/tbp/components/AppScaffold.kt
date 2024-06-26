package uk.co.goingproprogramming.tbp.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AppScaffold(
    title: String,
    defaultPadding: Dp = 16.dp,
    enableVerticalScroll: Boolean = false,
    onBack: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val subModifier = if (enableVerticalScroll)
        Modifier.verticalScroll(rememberScrollState())
    else
        Modifier

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            AppTopAppBar(
                title = title,
                onBack = onBack,
            )
        }
    ) { paddingValue ->
        Column(
            modifier = Modifier
                .padding(paddingValue)
                .padding(defaultPadding)
                .fillMaxSize()
                .then(subModifier),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            content()
        }
    }
}