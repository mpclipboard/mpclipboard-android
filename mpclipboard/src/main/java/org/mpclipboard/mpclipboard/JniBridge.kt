package org.mpclipboard.mpclipboard

import android.content.Context

internal class JniBridge {
    companion object {
        external fun mpclipboardSetup(context: Context)
        external fun mpclipboardConfigNew(uri: String, token: String, name: String): Long
        external fun mpclipboardStartThread(config: Long): Long
        external fun mpclipboardStopThread(handle: Long): Boolean
        external fun mpclipboardSend(handle: Long, text: String): Boolean
        external fun mpclipboardPoll(handle: Long): PollOutput
        external fun mpclipboardTakeFd(handle: Long): Long

        fun init() {
            System.loadLibrary("clipboardjni")
        }
    }
}