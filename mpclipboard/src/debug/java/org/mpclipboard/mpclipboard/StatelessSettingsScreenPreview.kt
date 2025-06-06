package org.mpclipboard.mpclipboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
internal fun StatelessSettingsScreenPreview() {
    StatelessSettingsScreen(
        endpoint = "ws://localhost:3000",
        setEndpoint = {},
        token = "test-token-123",
        setToken = {},
        onSubmit = {},
        connectivity = true,
        last5Messages = listOf("one", "two", "three", "four", "five")
    )
}