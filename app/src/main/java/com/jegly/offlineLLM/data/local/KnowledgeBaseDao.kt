package com.jegly.offlineLLM.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface KnowledgeBaseDao {
    @Query("SELECT * FROM knowledge_base ORDER BY createdAt DESC")
    fun getAllDocuments(): Flow<List<KnowledgeBaseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(entity: KnowledgeBaseEntity)

    @Query("DELETE FROM knowledge_base WHERE filePath = :filePath")
    suspend fun deleteDocument(filePath: String)
}