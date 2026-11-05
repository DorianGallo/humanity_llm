package com.jegly.offlineLLM.data.repository

import android.content.Context
import com.jegly.offlineLLM.data.local.dao.ModelConfigDao
import com.jegly.offlineLLM.data.local.entities.ModelConfigEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
private data class ModelPreset(
    val presetKey: String,
    val name: String,
    val temperature: Float = 0.2f,
    val topP: Float = 0.85f,
    val topK: Int = 30,
    val repeatPenalty: Float = 1.08f,
    val contextSize: Int = 3072,
    val mirostat: Int = 2,
    val mirostatTau: Float = 3.0f,
    val mirostatEta: Float = 0.1f,
    val systemPrompt: String = ""
)

@Singleton
class ModelConfigRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dao: ModelConfigDao
) {
    private val json = Json { ignoreUnknownKeys = true }

    fun getAllConfigs(): Flow<List<ModelConfigEntity>> = dao.getAllConfigs()

    suspend fun getActiveConfig(): ModelConfigEntity? = dao.getActiveConfig()

    suspend fun setActive(id: Long) {
        dao.clearActive()
        dao.setActive(id)
    }

    suspend fun update(config: ModelConfigEntity) = dao.update(config)

    suspend fun seedIfEmpty() {
        if (dao.getCount() > 0) return

        val jsonText = context.assets.open("model_presets.json")
            .bufferedReader()
            .use { it.readText() }

        val presets = json.decodeFromString<List<ModelPreset>>(jsonText)
        presets.forEachIndexed { index, preset ->
            dao.insert(
                ModelConfigEntity(
                    presetKey = preset.presetKey,
                    name = preset.name,
                    temperature = preset.temperature,
                    topP = preset.topP,
                    topK = preset.topK,
                    repeatPenalty = preset.repeatPenalty,
                    contextSize = preset.contextSize,
                    mirostat = preset.mirostat,
                    mirostatTau = preset.mirostatTau,
                    mirostatEta = preset.mirostatEta,
                    systemPrompt = preset.systemPrompt,
                    isActive = index == 0
                )
            )
        }
    }
}
