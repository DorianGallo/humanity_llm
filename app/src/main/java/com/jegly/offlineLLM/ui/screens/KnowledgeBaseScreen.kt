package com.jegly.offlineLLM.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnowledgeBaseScreen(
    viewModel: KnowledgeBaseViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val documents by viewModel.documents.collectAsState()
    val context = LocalContext.current
    var showTitleDialog by remember { mutableStateOf<Uri?>(null) }
    var titleInput by remember { mutableStateOf("") }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            showTitleDialog = uri
            titleInput = uri.lastPathSegment?.substringAfterLast('/')?.substringBeforeLast('.') ?: "New Document"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Knowledge Base") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Text("←", style = MaterialTheme.typography.titleLarge)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { filePickerLauncher.launch("*/*") }) {
                Icon(Icons.Default.Add, contentDescription = "Add Document")
            }
        }
    ) { padding ->
        if (documents.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No documents indexed. Tap + to add one.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(documents, key = { it.filePath }) { doc ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(doc.title, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(doc.createdAt)),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            IconButton(onClick = { viewModel.deleteDocument(doc.filePath) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showTitleDialog != null) {
        AlertDialog(
            onDismissRequest = { showTitleDialog = null },
            title = { Text("Document Title") },
            text = {
                OutlinedTextField(
                    value = titleInput,
                    onValueChange = { titleInput = it },
                    label = { Text("Title") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.ingestDocument(context, showTitleDialog!!, titleInput)
                    showTitleDialog = null
                }) { Text("Ingest") }
            },
            dismissButton = {
                TextButton(onClick = { showTitleDialog = null }) { Text("Cancel") }
            }
        )
    }
}