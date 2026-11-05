package com.jegly.offlineLLM.ui.screens

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jegly.offlineLLM.data.ingestion.ZimIngestionManager
import com.jegly.offlineLLM.data.local.KnowledgeBaseEntity
import com.jegly.offlineLLM.data.repository.KnowledgeBaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class KnowledgeBaseViewModel @Inject constructor(
    private val repository: KnowledgeBaseRepository
) : ViewModel() {

    val documents: StateFlow<List<KnowledgeBaseEntity>> = repository.getAllDocuments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun ingestDocument(context: Context, uri: Uri, title: String) {
        viewModelScope.launch {
            val result = ZimIngestionManager.ingestDocument(context, uri, title)
            result.onSuccess { file ->
                repository.addDocument(file.absolutePath, title)
            }
        }
    }

    fun deleteDocument(filePath: String) {
        viewModelScope.launch {
            repository.removeDocument(filePath)
            File(filePath).delete()
        }
    }
}