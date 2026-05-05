package com.jegly.offlineLLM.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.jegly.offlineLLM.ai.KiwixClient
import kotlinx.coroutines.delay

@Composable
fun ZimStatusIndicator(
    kiwixClient: KiwixClient,
    modifier: Modifier = Modifier,
) {
    var isConnected by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            isConnected = kiwixClient.isReachable()
            delay(10_000)
        }
    }

    ZimStatusIndicator(isConnected = isConnected, modifier = modifier)
}

@Composable
fun ZimStatusIndicator(
    isConnected: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(if (isConnected) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error)
            .semantics { contentDescription = if (isConnected) "ZIM: connected" else "ZIM: offline" }
    )
}
