package com.tpdoc.app.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChecklistDao {

    @Query("SELECT * FROM checklist_items ORDER BY id")
    fun observeAll(): Flow<List<ChecklistItem>>

    @Query("SELECT * FROM checklist_items WHERE category = :category ORDER BY id")
    fun observeByCategory(category: String): Flow<List<ChecklistItem>>

    @Query("SELECT COUNT(*) FROM checklist_items")
    suspend fun countAll(): Int

    @Insert
    suspend fun insertAll(items: List<ChecklistItem>)

    @Query("UPDATE checklist_items SET checked = :checked WHERE id = :id")
    suspend fun setChecked(id: Long, checked: Boolean)

    @Query("DELETE FROM checklist_items")
    suspend fun clearAll()
}