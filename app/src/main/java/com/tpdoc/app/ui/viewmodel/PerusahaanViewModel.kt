package com.tpdoc.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tpdoc.app.data.room.Perusahaan
import com.tpdoc.app.data.room.PerusahaanRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PerusahaanListUiState(
    val all: List<Perusahaan> = emptyList(),
    val filtered: List<Perusahaan> = emptyList(),
    val searchQuery: String = "",
    val filterStatus: String? = null,
    val filterNegara: String? = null,
    val isSearching: Boolean = false,
    val availableNegara: List<String> = emptyList(),
)

@OptIn(ExperimentalCoroutinesApi::class)
class PerusahaanViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = PerusahaanRepository(application)

    private val _searchQuery = MutableStateFlow("")
    private val _filterStatus = MutableStateFlow<String?>(null)
    private val _filterNegara = MutableStateFlow<String?>(null)
    private val _isSearching = MutableStateFlow(false)

    val uiState: StateFlow<PerusahaanListUiState> = combine(
        _searchQuery,
        _filterStatus,
        _filterNegara,
        _isSearching,
        repo.semuaNegara,
    ) { query, status, negara, searching, negaraList ->
        PerusahaanListUiState(
            searchQuery = query,
            filterStatus = status,
            filterNegara = negara,
            isSearching = searching,
            availableNegara = negaraList,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PerusahaanListUiState())

    val listFlow = combine(_searchQuery, _filterStatus, _filterNegara, _isSearching) { query, status, negara, searching ->
        Triple(query, status, negara)
    }.flatMapLatest { (query, status, negara) ->
        when {
            query.isNotBlank() -> repo.search(query)
            status != null || negara != null -> repo.filter(status, negara)
            else -> repo.semua
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val indukList = repo.induk.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun search(query: String) {
        _searchQuery.value = query
        _isSearching.value = query.isNotBlank()
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _isSearching.value = false
    }

    fun setFilter(status: String?, negara: String?) {
        _filterStatus.value = status
        _filterNegara.value = negara
    }

    fun clearFilters() {
        _filterStatus.value = null
        _filterNegara.value = null
    }

    fun delete(perusahaan: Perusahaan) {
        viewModelScope.launch { repo.delete(perusahaan) }
    }

    fun deleteById(id: Long) {
        viewModelScope.launch { repo.deleteById(id) }
    }

    fun duplicate(source: Perusahaan) {
        viewModelScope.launch {
            val copy = source.copy(
                id = 0,
                nama = source.nama + " - Copy",
                npwp = "-",
            )
            repo.insert(copy)
        }
    }
}