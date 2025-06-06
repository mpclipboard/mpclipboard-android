package org.mpclipboard.mpclipboard

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class MPClipboard {
    companion object {
        private var isInitialized = false
        private var isRunning = false
        private var config: Long? = null
        private var poller: Poller? = null

        fun reconfigure(context: Context, endpoint: String, token: String, name: String) {
            if (!isInitialized) {
                log("initializing")
                JniBridge.init()
                JniBridge.mpclipboardSetup(context)
                isInitialized = true
                log("initialization completed")
            }

            if (isRunning) {
                log("stopping currently running thread...")
                JniBridge.mpclipboardStopThread()
            }
            log("creating config with $endpoint / $name")
            val newConfig = JniBridge.mpclipboardConfigNew(endpoint, token, name)
            log("config has been created, starting thread...")
            JniBridge.mpclipboardStartThread(newConfig)
            log("thread has started")
            config = newConfig
            isRunning = true
        }

        private fun prefs(context: Context): SharedPreferences {
            return context.getSharedPreferences("mpclipboard-prefs", Context.MODE_PRIVATE)
        }

        fun loadPref(context: Context, key: String): String {
            return prefs(context).getString(key, "") ?: ""
        }

        fun savePrefs(context: Context, endpoint: String, token: String) {
            prefs(context).edit {
                putString("endpoint", endpoint)
                putString("token", token)
            }
        }

        fun reconfigure(context: Context, name: String) {
            val endpoint = loadPref(context, "endpoint")
            val token = loadPref(context, "token")
            reconfigure(context, endpoint, token, name)
        }

        fun stop() {
            if (isRunning) {
                JniBridge.mpclipboardStopThread()
                isRunning = false
            }
        }

        fun send(text: String) {
            JniBridge.mpclipboardSend(text)
        }

        fun startPolling(
            onConnectivityChanged: (Boolean) -> Unit,
            onClipboardChanged: (String) -> Unit
        ) {
            val poller = Poller(onConnectivityChanged, onClipboardChanged)
            poller.start()
            this.poller = poller
        }

        fun stopPolling() {
            this.poller?.stop()
            this.poller = null
        }
    }
}