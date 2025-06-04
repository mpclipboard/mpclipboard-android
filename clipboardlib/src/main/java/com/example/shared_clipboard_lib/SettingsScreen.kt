package com.example.shared_clipboard_lib

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit

const val serviceName = "shared-clipboard-settings"
internal fun getPref(prefs: SharedPreferences, key: String): String {
    return prefs.getString(key, "") ?: ""
}

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences("shared_clipboard_prefs", Context.MODE_PRIVATE)
    }

    var endpoint by remember { mutableStateOf(getPref(prefs, "endpoint")) }
    var token by remember { mutableStateOf(getPref(prefs, "token")) }
    var connectivity by remember { mutableStateOf(false) }
    var last5Messages by remember { mutableStateOf(List(5) { "" }) }

    LaunchedEffect(Unit) {
        SharedClipboard.reconfigure(endpoint, token, serviceName)
        SharedClipboard.startPolling(
            onConnectivityChanged = { connectivity = it },
            onClipboardChanged = { last5Messages = last5Messages.drop(1) + it }
        )
    }

    StatelessSettingsScreen(
        endpoint = endpoint,
        setEndpoint = { endpoint = it },
        token = token,
        setToken = { token = it },
        onSubmit = {
            prefs.edit {
                putString("endpoint", endpoint)
                putString("token", token)
            }
            SharedClipboard.reconfigure(endpoint, token, serviceName)
        },
        connectivity = connectivity,
        last5Messages = last5Messages,
        modifier = modifier
    )
}