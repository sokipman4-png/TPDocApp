package com.tpdoc.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.tpdoc.app.data.ContentData

/** Filter materi; logika pencarian murni sehingga mudah diuji. */
class PencarianViewModel : ViewModel() {

    var query by mutableStateOf("")
        private set

    fun updateQuery(q: String) {
        query = q
    }

    fun hasil(): List<ContentData.MateriItem> = filterMateri(ContentData.allSearchItems, query)

    companion object {
        fun filterMateri(
            items: List<ContentData.MateriItem>,
            query: String,
        ): List<ContentData.MateriItem> {
            val q = query.trim()
            if (q.isEmpty()) return emptyList()
            val lower = q.lowercase()
            return items.filter { item ->
                item.title.lowercase().contains(lower) ||
                    item.body.lowercase().contains(lower) ||
                    item.section.lowercase().contains(lower) ||
                    item.keywords.any { it.lowercase().contains(lower) }
            }
        }
    }
}