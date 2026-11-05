package com.jegly.offlineLLM.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jegly.offlineLLM.zim.ZimEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZimReaderScreen(
    zimPath: String,
    entryPath: String,
    onNavigateBack: () -> Unit
) {
    var content by remember { mutableStateOf("Loading...") }
    var title by remember { mutableStateOf("ZIM Reader") }

    LaunchedEffect(zimPath, entryPath) {
        if (entryPath.isBlank()) {
            content = "No entry selected. Please open a specific article from the knowledge base."
            return@LaunchedEffect
        }
        withContext(Dispatchers.IO) {
            try {
                title = entryPath.substringAfterLast("/").ifBlank { "ZIM Reader" }
                content = ZimEngine.readZimEntry(zimPath, entryPath)
                if (content.startsWith("Error:")) {
                    content = "Failed to load article: $content"
                }
            } catch (e: Exception) {
                content = "Failed to load article: ${e.message}"
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}