package com.example.shared_clipboard_lib

import android.os.Handler
import android.os.Looper

internal class Poller(
    private val onConnectivityChanged: (Boolean) -> Unit,
    private val onClipboardChanged: (String) -> Unit,
) {
    private val handler = Handler(Looper.getMainLooper())
    private val pollRunnable = object : Runnable {
        override fun run() {
            val output = JniBridge.mpclipboardPoll()
            if (output.connectivity != null) {
                log("connectivity changed: ${output.connectivity}")
                onConnectivityChanged(output.connectivity)
            }
            if (output.text != null) {
                log("text changed: ${output.text}")
                onClipboardChanged(output.text)
            }
            handler.postDelayed(this, 100L)
        }
    }

    fun start() {
        handler.post(pollRunnable)
    }

    fun stop() {
        handler.removeCallbacks(pollRunnable)
    }
}