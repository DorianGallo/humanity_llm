package com.jegly.offlineLLM.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jegly.offlineLLM.data.local.entities.ModelConfigEntity
import com.jegly.offlineLLM.data.repository.ModelConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModelConfigViewModel @Inject constructor(
    private val repository: ModelConfigRepository,
) : ViewModel() {

    val configs: StateFlow<List<ModelConfigEntity>> = repository.getAllConfigs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val activeConfigId: StateFlow<Long?> = configs
        .map { list -> list.find { it.isActive }?.id }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun setActive(id: Long) {
        viewModelScope.launch { repository.setActive(id) }
    }

    fun update(config: ModelConfigEntity) {
        viewModelScope.launch { repository.update(config) }
    }
}
