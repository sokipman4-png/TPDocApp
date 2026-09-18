package com.tpdoc.app.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ModelCacheDao {

    @Query("SELECT * FROM model_cache ORDER BY name")
    fun observeAll(): Flow<List<ModelCache>>

    @Query("SELECT * FROM model_cache WHERE id LIKE '%' || :query || '%' OR name LIKE '%' || :query || '%' ORDER BY name")
    fun search(query: String): Flow<List<ModelCache>>

    @Query("SELECT * FROM model_cache ORDER BY name")
    suspend fun getAll(): List<ModelCache>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(models: List<ModelCache>)

    @Query("DELETE FROM model_cache")
    suspend fun clearAll()

    @Query("SELECT MAX(cachedAt) FROM model_cache")
    suspend fun lastCachedAt(): Long?

    @Query("SELECT COUNT(*) FROM model_cache")
    suspend fun count(): Int
}