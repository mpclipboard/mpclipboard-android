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

    var endpoint by remember { mutableStateOf(MPClipboard.loadPref(context, "endpoint")) }
    var token by remember { mutableStateOf(MPClipboard.loadPref(context, "token")) }
    var connectivity by remember { mutableStateOf(false) }
    var last5Messages by remember { mutableStateOf(List(5) { "" }) }

    DisposableEffect(Unit) {
        log("starting SettingsScreen effect")
        MPClipboard.reconfigure(context, endpoint, token, serviceName)
        MPClipboard.startPolling(
            onConnectivityChanged = { connectivity = it },
            onClipboardChanged = { last5Messages = last5Messages.drop(1) + it }
        )

        onDispose {
            log("stopping SettingsScreen effect")
            MPClipboard.stopPolling()
            MPClipboard.stop()
        }
    }

    StatelessSettingsScreen(
        endpoint = endpoint,
        setEndpoint = { endpoint = it },
        token = token,
        setToken = { token = it },
        onSubmit = {
            MPClipboard.savePrefs(context, endpoint, token)
            MPClipboard.reconfigure(context, endpoint, token, serviceName)
        },
        connectivity = connectivity,
        last5Messages = last5Messages,
        modifier = modifier
    )
}