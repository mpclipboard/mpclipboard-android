package org.mpclipboard.mpclipboard

import android.content.Context

class MPClipboard(uri: String, token: String, name: String) {
    companion object {
        private var isInitialized = false

        fun init(context: Context) {
            if (!isInitialized) {
                log("initializing")
                JniBridge.init()
                JniBridge.mpclipboardSetup(context)
                log("initialization completed")
            }
        }
    }

    private var config: Long
    private var handle: Long
    var fd: Long

    init {
        log("creating config with $uri / $name")
        config = JniBridge.mpclipboardConfigNew(uri, token, name)
        log("starting thread...")
        handle = JniBridge.mpclipboardStartThread(config)
        fd = JniBridge.mpclipboardTakeFd(handle)
    }

    fun stop() {
        log("stopping thread...")
        JniBridge.mpclipboardStopThread(handle)
        poller?.stop()
    }

    fun send(text: String): Boolean {
        return JniBridge.mpclipboardSend(handle, text)
    }

    private var poller: Poller? = null
    fun startPolling(onConnectivityChanged: (Boolean) -> Unit, onClipboardChanged: (String) -> Unit) {
        poller = Poller(handle, onConnectivityChanged, onClipboardChanged)
        poller?.start()
    }
}