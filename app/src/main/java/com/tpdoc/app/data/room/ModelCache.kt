package com.tpdoc.app.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "model_cache")
data class ModelCache(
    @PrimaryKey val id: String,
    val name: String,
    val promptPrice: String,
    val completionPrice: String,
    val contextLength: Int,
    val cachedAt: Long = System.currentTimeMillis(),
)