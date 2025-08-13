package org.mpclipboard.mpclipboard

import android.content.Context
import androidx.core.content.edit

class Prefs(val context: Context) {
    fun readToken(): String {
        return getPref("token")
    }
    fun writeToken(token: String) {
        setPref("token", token)
    }
    fun readURI(): String {
        return getPref("uri")
    }
    fun writeURI(uri: String) {
        setPref("uri", uri)
    }

    private val serviceName = "mpclipboard-settings"
    private val prefs = context.getSharedPreferences(serviceName, Context.MODE_PRIVATE)

    private fun getPref(key: String): String {
        return prefs.getString(key, "") ?: ""
    }
    private fun setPref(key: String, value: String) {
        prefs.edit { putString(key, value) }
    }

}