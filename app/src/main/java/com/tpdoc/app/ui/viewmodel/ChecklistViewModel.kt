package com.tpdoc.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tpdoc.app.data.room.ChecklistItem
import com.tpdoc.app.data.room.ChecklistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ChecklistViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = ChecklistRepository(application)

    val berelasi: Flow<List<ChecklistItem>> = repo.berelasi
    val dokumen: Flow<List<ChecklistItem>> = repo.dokumen
    val persiapan: Flow<List<ChecklistItem>> = repo.persiapan
    val semua: Flow<List<ChecklistItem>> = repo.semua

    init {
        viewModelScope.launch { repo.seedIfEmpty() }
    }

    fun toggle(item: ChecklistItem) {
        viewModelScope.launch { repo.toggle(item) }
    }

    fun resetSemua() {
        viewModelScope.launch { repo.reseed() }
    }
}