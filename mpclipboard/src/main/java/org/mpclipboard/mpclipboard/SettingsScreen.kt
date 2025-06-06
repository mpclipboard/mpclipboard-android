package org.mpclipboard.mpclipboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

const val serviceName = "mpclipboard-settings"

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    var endpoint by remember { mutableStateOf(SharedClipboard.loadPref(context, "endpoint")) }
    var token by remember { mutableStateOf(SharedClipboard.loadPref(context, "token")) }
    var connectivity by remember { mutableStateOf(false) }
    var last5Messages by remember { mutableStateOf(List(5) { "" }) }

    DisposableEffect(Unit) {
        log("starting SettingsScreen effect")
        SharedClipboard.reconfigure(context, endpoint, token, serviceName)
        SharedClipboard.startPolling(
            onConnectivityChanged = { connectivity = it },
            onClipboardChanged = { last5Messages = last5Messages.drop(1) + it }
        )

        onDispose {
            log("stopping SettingsScreen effect")
            SharedClipboard.stopPolling()
            SharedClipboard.stop()
        }
    }

    StatelessSettingsScreen(
        endpoint = endpoint,
        setEndpoint = { endpoint = it },
        token = token,
        setToken = { token = it },
        onSubmit = {
            SharedClipboard.savePrefs(context, endpoint, token)
            SharedClipboard.reconfigure(context, endpoint, token, serviceName)
        },
        connectivity = connectivity,
        last5Messages = last5Messages,
        modifier = modifier
    )
}