package com.example.shared_clipboard_lib

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun Form(endpoint: String, setEndpoint: (String) -> Unit, token: String, setToken: (String) -> Unit, onSubmit: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = "Settings", style = MaterialTheme.typography.headlineMedium)

        Text(text = "Endpoint")
        TextField(
            value = endpoint,
            onValueChange = setEndpoint,
            placeholder = { Text(text = "Enter endpoint") },
            modifier = Modifier.fillMaxWidth()
        )

        Text(text = "Token")
        TextField(
            value = token,
            onValueChange = setToken,
            placeholder = { Text(text = "Enter token") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth(),
            shape = ShapeDefaults.ExtraLarge
        ) {
            Text(text = "Save")
        }
    }
}