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

    private var config: Long = 0
    private var handle: Long = 0
    var fd: Long = 0

    init {
        log("creating config with $uri / $name")
        config = JniBridge.mpclipboardConfigNew(uri, token, name)
        if (config != 0L) {
            log("starting thread...")
            handle = JniBridge.mpclipboardStartThread(config)
            if (handle != 0L) {
                fd = JniBridge.mpclipboardTakeFd(handle)
            }
        }
    }

    fun stop() {
        log("stopping thread...")
        if (handle == 0L) {
            return
        }
        JniBridge.mpclipboardStopThread(handle)
        poller?.stop()
    }

    fun send(text: String): Boolean {
        if (handle == 0L) {
            return false
        }
        return JniBridge.mpclipboardSend(handle, text)
    }

    private var poller: Poller? = null
    fun startPolling(onConnectivityChanged: (Boolean) -> Unit, onClipboardChanged: (String) -> Unit) {
        if (handle == 0L) {
            return
        }
        poller = Poller(handle, onConnectivityChanged, onClipboardChanged)
        poller?.start()
    }
}