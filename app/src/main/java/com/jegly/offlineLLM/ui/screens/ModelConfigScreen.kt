package com.jegly.offlineLLM.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jegly.offlineLLM.data.local.entities.ModelConfigEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelConfigScreen(
    onNavigateBack: () -> Unit,
    viewModel: ModelConfigViewModel = hiltViewModel(),
) {
    val configs by viewModel.configs.collectAsStateWithLifecycle()
    val activeId by viewModel.activeConfigId.collectAsStateWithLifecycle()
    var editTarget by remember { mutableStateOf<ModelConfigEntity?>(null) }

    if (editTarget != null) {
        ModelConfigEditScreen(
            config = editTarget!!,
            onSave = { updated ->
                viewModel.update(updated)
                editTarget = null
            },
            onBack = { editTarget = null },
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Model Presets") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            configs.forEach { config ->
                ModelConfigCard(
                    config = config,
                    isActive = config.id == activeId,
                    onSetActive = { viewModel.setActive(config.id) },
                    onEdit = { editTarget = config },
                )
            }
        }
    }
}

@Composable
private fun ModelConfigCard(
    config: ModelConfigEntity,
    isActive: Boolean,
    onSetActive: () -> Unit,
    onEdit: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = config.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                if (isActive) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = "Active",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            Text(
                "Temp: ${config.temperature}  TopP: ${config.topP}  TopK: ${config.topK}  RepeatPenalty: ${config.repeatPenalty}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                "Context: ${config.contextSize} tokens",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (config.modelInfoId == null) {
                Text(
                    "No GGUF linked yet — import a model in Settings",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!isActive) {
                    Button(onClick = onSetActive) { Text("Set Active") }
                }
                Button(onClick = onEdit) { Text("Edit") }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModelConfigEditScreen(
    config: ModelConfigEntity,
    onSave: (ModelConfigEntity) -> Unit,
    onBack: () -> Unit,
) {
    var temperature by remember { mutableFloatStateOf(config.temperature) }
    var topP by remember { mutableFloatStateOf(config.topP) }
    var topK by remember { mutableIntStateOf(config.topK) }
    var repeatPenalty by remember { mutableFloatStateOf(config.repeatPenalty) }
    var contextSize by remember { mutableIntStateOf(config.contextSize) }
    var systemPrompt by remember { mutableStateOf(config.systemPrompt) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit: ${config.name}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            onSave(
                                config.copy(
                                    temperature = temperature,
                                    topP = topP,
                                    topK = topK,
                                    repeatPenalty = repeatPenalty,
                                    contextSize = contextSize,
                                    systemPrompt = systemPrompt,
                                )
                            )
                        },
                        modifier = Modifier.padding(end = 8.dp),
                    ) { Text("Save") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ConfigSlider("Temperature", temperature, 0f..2f) { temperature = "%.2f".format(it).toFloat() }
            ConfigSlider("Top-P", topP, 0f..1f) { topP = "%.2f".format(it).toFloat() }
            ConfigSlider("Top-K (${topK})", topK.toFloat(), 1f..100f) { topK = it.toInt() }
            ConfigSlider("Repeat Penalty", repeatPenalty, 1f..2f) { repeatPenalty = "%.2f".format(it).toFloat() }
            ConfigSlider("Context Size (${contextSize})", contextSize.toFloat(), 512f..8192f) { contextSize = it.toInt() }

            Text("System Prompt", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = systemPrompt,
                onValueChange = { systemPrompt = it },
                modifier = Modifier.fillMaxWidth().height(180.dp),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ConfigSlider(label: String, value: Float, range: ClosedFloatingPointRange<Float>, onValueChange: (Float) -> Unit) {
    Column {
        Text("$label: ${"%.2f".format(value)}", style = MaterialTheme.typography.bodyMedium)
        Slider(value = value, onValueChange = onValueChange, valueRange = range)
    }
}
