package com.tpdoc.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tpdoc.app.data.room.Perusahaan
import com.tpdoc.app.data.room.PerusahaanRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

enum class StatusKepatuhan { HIJAU, KUNING, MERAH }

data class PerusahaanSummary(
    val perusahaan: Perusahaan,
    val statusKepatuhan: StatusKepatuhan,
    val statusLabel: String,
)

data class DashboardUiState(
    val summaries: List<PerusahaanSummary> = emptyList(),
    val totalPerusahaan: Int = 0,
    val hijau: Int = 0,
    val kuning: Int = 0,
    val merah: Int = 0,
    val filterTahunPajak: Int? = null,
    val filterStatus: String? = null,
    val filterNegara: String? = null,
    val availableTahunPajak: List<Int> = emptyList(),
    val availableNegara: List<String> = emptyList(),
    val hierarchyData: List<Triple<Perusahaan, List<Perusahaan>, List<Perusahaan>>> = emptyList(),
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = PerusahaanRepository(application)

    val uiState: StateFlow<DashboardUiState> = repo.semua.map { list ->
        val summaries = list.map { p ->
            // Simplified: status based on whether company has children (induk/anak)
            // In full implementation this would check actual TP Doc compliance
            val statusKepatuhan = when {
                p.status == Perusahaan.STATUS_INDUK -> StatusKepatuhan.HIJAU
                p.status == Perusahaan.STATUS_ANAK || p.status == Perusahaan.STATUS_CUCU -> StatusKepatuhan.KUNING
                else -> StatusKepatuhan.MERAH
            }
            val statusLabel = when (statusKepatuhan) {
                StatusKepatuhan.HIJAU -> "Patuh"
                StatusKepatuhan.KUNING -> "Perlu Tindakan"
                StatusKepatuhan.MERAH -> "Belum Patuh"
            }
            PerusahaanSummary(perusahaan = p, statusKepatuhan = statusKepatuhan, statusLabel = statusLabel)
        }

        val hijau = summaries.count { it.statusKepatuhan == StatusKepatuhan.HIJAU }
        val kuning = summaries.count { it.statusKepatuhan == StatusKepatuhan.KUNING }
        val merah = summaries.count { it.statusKepatuhan == StatusKepatuhan.MERAH }

        // Build hierarchy: induk -> anak -> cucu
        val hierarchyData = list.filter { it.parentId == null }.map { induk ->
            val anakList = list.filter { it.parentId == induk.id }
            val cucuList = anakList.flatMap { anak -> list.filter { it.parentId == anak.id } }
            Triple(induk, anakList, cucuList)
        }

        DashboardUiState(
            summaries = summaries,
            totalPerusahaan = list.size,
            hijau = hijau,
            kuning = kuning,
            merah = merah,
            availableTahunPajak = list.map { it.tahunPajak }.distinct().sorted(),
            availableNegara = list.map { it.negara }.distinct().sorted(),
            hierarchyData = hierarchyData,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState())
}