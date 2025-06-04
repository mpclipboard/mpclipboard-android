package com.example.shared_clipboard_lib

class SharedClipboard {
    companion object {
        private var isInitialized = false
        private var isRunning = false
        private var config: Long? = null
        private var poller: Poller? = null

        fun reconfigure(endpoint: String, token: String, name: String) {
            if (!isInitialized) {
                log("initializing")
                JniBridge.init()
                JniBridge.shared_clipboard_setup()
                isInitialized = true
                log("initialization completed")
            }

            if (isRunning) {
                log("stopping currently running thread...")
                JniBridge.shared_clipboard_stop_thread()
            }
            log("creating config with $endpoint / $name")
            val newConfig = JniBridge.shared_clipboard_config_new(endpoint, token, name)
            log("config has been created, starting thread...")
            JniBridge.shared_clipboard_start_thread(newConfig)
            log("thread has started")
            config = newConfig
            isRunning = true
        }

        fun stop() {
            if (isRunning) {
                JniBridge.shared_clipboard_stop_thread()
                isRunning = false
            }
        }

        fun send(text: String) {
            JniBridge.shared_clipboard_send(text)
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