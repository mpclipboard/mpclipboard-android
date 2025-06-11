package org.mpclipboard.mpclipboard

import androidx.compose.runtime.Composable
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 200, heightDp = 150)
@Composable
internal fun PreviewConnected() {
    StatelessConnectivityWidget(true)
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 200, heightDp = 150)
@Composable
internal fun PreviewDisconnected() {
    StatelessConnectivityWidget(false)
}