package com.jegly.offlineLLM.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jegly.offlineLLM.data.local.entities.ModelConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ModelConfigDao {

    @Query("SELECT * FROM model_configs ORDER BY id ASC")
    fun getAllConfigs(): Flow<List<ModelConfigEntity>>

    @Query("SELECT * FROM model_configs WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveConfig(): ModelConfigEntity?

    @Query("SELECT * FROM model_configs WHERE presetKey = :key LIMIT 1")
    suspend fun getByPresetKey(key: String): ModelConfigEntity?

    @Query("SELECT COUNT(*) FROM model_configs")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(config: ModelConfigEntity): Long

    @Update
    suspend fun update(config: ModelConfigEntity)

    @Delete
    suspend fun delete(config: ModelConfigEntity)

    @Query("UPDATE model_configs SET isActive = 0")
    suspend fun clearActive()

    @Query("UPDATE model_configs SET isActive = 1 WHERE id = :id")
    suspend fun setActive(id: Long)
}
