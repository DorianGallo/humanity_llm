package com.jegly.offlineLLM.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "knowledge_base")
data class KnowledgeBaseEntity(
    @PrimaryKey val filePath: String,
    val title: String,
    val createdAt: Long = System.currentTimeMillis()
)