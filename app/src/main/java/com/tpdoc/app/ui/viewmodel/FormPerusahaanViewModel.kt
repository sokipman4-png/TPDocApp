package com.tpdoc.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tpdoc.app.data.room.Perusahaan
import com.tpdoc.app.data.room.PerusahaanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FormUiState(
    val id: Long = 0,
    val nama: String = "",
    val npwp: String = "",
    val alamat: String = "",
    val negara: String = "Indonesia",
    val status: String = Perusahaan.STATUS_INDUK,
    val parentId: Long? = null,
    val tahunPajak: Int = 2024,
    val logoPath: String? = null,
    val isEdit: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null,
    val daftarInduk: List<Perusahaan> = emptyList(),
)

class FormPerusahaanViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = PerusahaanRepository(application)

    private val _uiState = MutableStateFlow(FormUiState())
    val uiState: StateFlow<FormUiState> = _uiState.asStateFlow()

    fun loadForEdit(id: Long) {
        viewModelScope.launch {
            val p = repo.getById(id)
            if (p != null) {
                _uiState.value = FormUiState(
                    id = p.id,
                    nama = p.nama,
                    npwp = p.npwp,
                    alamat = p.alamat,
                    negara = p.negara,
                    status = p.status,
                    parentId = p.parentId,
                    tahunPajak = p.tahunPajak,
                    logoPath = p.logoPath,
                    isEdit = true,
                    daftarInduk = repo.getAll().filter { it.id != p.id && it.status == Perusahaan.STATUS_INDUK },
                )
            } else {
                _uiState.value = FormUiState(daftarInduk = repo.getAll().filter { it.status == Perusahaan.STATUS_INDUK })
            }
        }
    }

    fun loadDaftarInduk() {
        viewModelScope.launch {
            _uiState.update { it.copy(daftarInduk = repo.getAll().filter { p -> p.status == Perusahaan.STATUS_INDUK }) }
        }
    }

    fun updateNama(v: String) { _uiState.update { it.copy(nama = v) } }
    fun updateNpwp(v: String) { _uiState.update { it.copy(npwp = v) } }
    fun updateAlamat(v: String) { _uiState.update { it.copy(alamat = v) } }
    fun updateNegara(v: String) { _uiState.update { it.copy(negara = v) } }
    fun updateStatus(v: String) { _uiState.update { it.copy(status = v) } }
    fun updateParentId(v: Long?) { _uiState.update { it.copy(parentId = v) } }
    fun updateTahunPajak(v: Int) { _uiState.update { it.copy(tahunPajak = v) } }
    fun updateLogoPath(v: String?) { _uiState.update { it.copy(logoPath = v) } }

    fun validate(): String? {
        val s = _uiState.value
        return when {
            s.nama.isBlank() -> "Nama perusahaan wajib diisi"
            else -> null
        }
    }

    fun save() {
        val s = _uiState.value
        val err = validate()
        if (err != null) {
            _uiState.update { it.copy(error = err) }
            return
        }
        viewModelScope.launch {
            try {
                val p = Perusahaan(
                    id = s.id,
                    nama = s.nama.trim(),
                    npwp = s.npwp.ifBlank { "-" },
                    alamat = s.alamat.trim(),
                    negara = s.negara.trim().ifBlank { "Indonesia" },
                    status = s.status,
                    parentId = s.parentId,
                    tahunPajak = s.tahunPajak,
                    logoPath = s.logoPath,
                )
                if (s.isEdit) repo.update(p) else repo.insert(p)
                _uiState.update { it.copy(saved = true, error = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Gagal menyimpan") }
            }
        }
    }
}