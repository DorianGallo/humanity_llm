package com.jegly.offlineLLM.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "model_configs",
    foreignKeys = [
        ForeignKey(
            entity = ModelInfo::class,
            parentColumns = ["id"],
            childColumns = ["modelInfoId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("modelInfoId")]
)
data class ModelConfigEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val presetKey: String,
    val name: String,
    val modelInfoId: Long? = null,
    val temperature: Float = 0.2f,
    val topP: Float = 0.85f,
    val topK: Int = 30,
    val repeatPenalty: Float = 1.08f,
    val contextSize: Int = 3072,
    val mirostat: Int = 2,
    val mirostatTau: Float = 3.0f,
    val mirostatEta: Float = 0.1f,
    val systemPrompt: String = "",
    val isActive: Boolean = false
)
