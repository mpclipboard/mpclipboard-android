package com.example.shared_clipboard_lib

internal class JniBridge {
    companion object {
        external fun shared_clipboard_setup()
        external fun shared_clipboard_start_thread(config: Long)
        external fun shared_clipboard_stop_thread(): Boolean
        external fun shared_clipboard_send(text: String)
        external fun shared_clipboard_poll(): PollOutput
        external fun shared_clipboard_config_new(url: String, token: String, name: String): Long

        fun init() {
            System.loadLibrary("clipboardjni")
        }
    }
}