package org.mpclipboard.mpclipboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding

@Composable
internal fun StatelessSettingsScreen(
    endpoint: String,
    setEndpoint: (String) -> Unit,
    token: String,
    setToken: (String) -> Unit,
    onSubmit: () -> Unit,
    connectivity: Boolean,
    last5Messages: List<String>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 48.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        Form(
            endpoint = endpoint,
            setEndpoint = setEndpoint,
            token = token,
            setToken = setToken,
            onSubmit = onSubmit
        )

        Connectivity(connectivity)

        RecentMessages(last5Messages)
    }
}

