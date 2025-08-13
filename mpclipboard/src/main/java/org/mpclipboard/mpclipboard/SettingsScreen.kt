package org.mpclipboard.mpclipboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val prefs = Prefs(context)

    var uri by remember { mutableStateOf(prefs.readURI()) }
    var token by remember { mutableStateOf(prefs.readToken()) }
    var mpclipboard by remember { mutableStateOf<MPClipboard?>(null) }

    var connectivity by remember { mutableStateOf(false) }
    var last5Messages by remember { mutableStateOf(List(5) { "" }) }

    fun disconnect() {
        log("disconnecting...")
        mpclipboard?.stop()
        mpclipboard = null
    }

    fun connect() {
        log("connecting...")
        mpclipboard = MPClipboard(uri, token, "mpclipboard-android-settings")
        mpclipboard?.startPolling(
            onConnectivityChanged = { connectivity = it },
            onClipboardChanged = { last5Messages = last5Messages.drop(1) + it }
        )
    }

    DisposableEffect(Unit) {
        connect()

        onDispose {
            disconnect()
        }
    }

    StatelessSettingsScreen(
        uri = uri,
        setURI = { uri = it },
        token = token,
        setToken = { token = it },
        onSubmit = {
            prefs.writeURI(uri)
            prefs.writeToken(token)
            disconnect()
            connect()
        },
        connectivity = connectivity,
        last5Messages = last5Messages,
        modifier = modifier
    )
}