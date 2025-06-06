package com.example.shared_clipboard_lib

import android.content.Context

internal class JniBridge {
    companion object {
        external fun mpclipboardSetup(context: Context)
        external fun mpclipboardStartThread(config: Long)
        external fun mpclipboardStopThread(): Boolean
        external fun mpclipboardSend(text: String)
        external fun mpclipboardPoll(): PollOutput
        external fun mpclipboardConfigNew(url: String, token: String, name: String): Long

        fun init() {
            System.loadLibrary("clipboardjni")
        }
    }
}