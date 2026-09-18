package com.tpdoc.app.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "checklist_items")
data class ChecklistItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,   // "berelasi", "dokumen", "persiapan"
    val title: String,
    val desc: String = "",
    val checked: Boolean = false,
) {
    companion object {
        const val CAT_BERELASI = "berelasi"
        const val CAT_DOKUMEN = "dokumen"
        const val CAT_PERSIAPAN = "persiapan"
    }
}