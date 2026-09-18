package com.tpdoc.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tpdoc.app.calc.ThresholdCalculator
import com.tpdoc.app.data.room.Perusahaan
import com.tpdoc.app.data.room.PerusahaanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AnalisisUiState(
    val perusahaan: Perusahaan? = null,
    val omzetGrup: String = "",
    val transaksiAfiliasi: String = "",
    val hasilThreshold: ThresholdCalculator.Result? = null,
    val checklistBerelasi: List<Pair<String, Boolean>> = listOf(
        "Kepemilikan saham >= 25%" to false,
        "Keluarga sedarah/semenda derajat 2" to false,
        "Direksi/komisaris yang sama" to false,
        "Ketergantungan keuangan/teknis" to false,
    ),
    val jenisTransaksi: List<Pair<String, Boolean>> = listOf(
        "Jual beli barang" to false,
        "Jasa manajemen" to false,
        "Pinjam-meminjam (bunga)" to false,
        "Royalti/lisensi merek" to false,
        "Sewa" to false,
    ),
    val wajibMasterFile: Boolean? = null,
    val wajibLocalFile: Boolean? = null,
    val wajibCbcr: Boolean? = null,
    val estimasiSanksi: String = "",
)

class AnalisisViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = PerusahaanRepository(application)

    private val _uiState = MutableStateFlow(AnalisisUiState())
    val uiState: StateFlow<AnalisisUiState> = _uiState.asStateFlow()

    fun loadPerusahaan(id: Long) {
        viewModelScope.launch {
            val p = repo.getById(id)
            _uiState.value = _uiState.value.copy(perusahaan = p)
        }
    }

    fun updateOmzet(v: String) {
        _uiState.value = _uiState.value.copy(omzetGrup = v)
        hitung()
    }

    fun updateTransaksi(v: String) {
        _uiState.value = _uiState.value.copy(transaksiAfiliasi = v)
        hitung()
    }

    fun toggleBerelasi(index: Int) {
        val list = _uiState.value.checklistBerelasi.toMutableList()
        list[index] = list[index].copy(second = !list[index].second)
        _uiState.value = _uiState.value.copy(checklistBerelasi = list)
        hitung()
    }

    fun toggleTransaksi(index: Int) {
        val list = _uiState.value.jenisTransaksi.toMutableList()
        list[index] = list[index].copy(second = !list[index].second)
        _uiState.value = _uiState.value.copy(jenisTransaksi = list)
        hitung()
    }

    private fun hitung() {
        val s = _uiState.value
        val omzet = s.omzetGrup.toLongOrNull() ?: 0L
        val transaksi = s.transaksiAfiliasi.toLongOrNull() ?: 0L

        val hasil = ThresholdCalculator.hitung(omzet, transaksi)
        val hasBerelasi = s.checklistBerelasi.any { it.second }
        val hasTransaksi = s.jenisTransaksi.any { it.second }

        val wajibMf = omzet > 50_000_000_000 && transaksi > 20_000_000_000
        val wajibLf = wajibMf
        val wajibCbcr = omzet > 11_000_000_000_000

        // Sanksi estimasi
        val sanksi = buildString {
            if (wajibMf && !hasBerelasi) {
                append("Resiko: Master File/Local File wajib tapi belum ada checklist berelasi.\n")
                append("Estimasi sanksi: denda Rp1.000.000 per dokumen tidak tersedia.\n")
            }
            if (wajibCbcr && !hasTransaksi) {
                append("Resiko: CbCR wajib tapi belum tercatat transaksi afiliasi.\n")
            }
            if (!wajibMf && !wajibCbcr) {
                append("Tidak ada kewajiban TP Doc formal. Prinsip kewajaran tetap berlaku.")
            }
            if (length == 0) {
                append("Semua dokumen sudah sesuai ketentuan.")
            }
        }

        _uiState.value = _uiState.value.copy(
            hasilThreshold = hasil,
            wajibMasterFile = wajibMf,
            wajibLocalFile = wajibLf,
            wajibCbcr = wajibCbcr,
            estimasiSanksi = sanksi,
        )
    }
}