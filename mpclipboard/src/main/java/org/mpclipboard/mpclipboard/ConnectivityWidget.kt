package org.mpclipboard.mpclipboard

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.currentState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConnectivityWidget : GlanceAppWidget() {
    companion object {
        val CONNECTED_KEY = booleanPreferencesKey("connected")

        fun update(context: Context, connected: Boolean) {
            CoroutineScope(Dispatchers.IO).launch {
                val glanceManager = GlanceAppWidgetManager(context)
                val ids = glanceManager.getGlanceIds(ConnectivityWidget::class.java)

                for (id in ids) {
                    updateAppWidgetState(context, id) { prefs ->
                        prefs[CONNECTED_KEY] = connected
                    }
                    ConnectivityWidget().update(context, id)
                }
            }
        }
    }
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val connected = prefs[CONNECTED_KEY] ?: false

            StatelessConnectivityWidget(connected)
        }
    }
}

class ConnectivityWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ConnectivityWidget()
}
