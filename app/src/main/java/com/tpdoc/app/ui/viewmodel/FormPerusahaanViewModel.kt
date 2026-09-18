package com.tpdoc.app.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tpdoc.app.data.room.Perusahaan
import com.tpdoc.app.data.room.PerusahaanRepository
import com.tpdoc.app.validation.FormFieldError
import com.tpdoc.app.validation.FormValidation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UiState form perusahaan (Tahap 2.6): Loading/Success/Error + fieldErrors inline.
 */
data class FormUiState(
    val id: Long = 0,
    val nama: String = "",
    val npwp: String = "",
    val alamat: String = "",
    val negara: String = "Indonesia",
    val status: String = Perusahaan.STATUS_INDUK,
    val parentId: Long? = null,
    val tahunPajakText: String = "2024",
    val logoPath: String? = null,
    val isEdit: Boolean = false,
    val loading: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null,
    val fieldErrors: Map<String, String> = emptyMap(),
    val daftarInduk: List<Perusahaan> = emptyList(),
)

/**
 * ViewModel form perusahaan — hardend volgens pelajaran Tahap 2.6:
 *  1. Validasi input SEPERIAN save ke database (FormValidation, pure).
 *  2. Save/update/load dibungkus try-catch -> snackbar/dialog ramah, kein crash.
 *  3. Null safety: `?.` + default waarde, geen `!!`.
 *  4. Coroutine: viewModelScope; DB-access via suspend DAO (Room executor).
 *  5. UiState: loading/saved/error + fieldErrors inline per field.
 *
 * BUG 1 fix: crash bij simpan werd veroorzaakt door onbestuurde paden
 * (geen validatie → constraint/NPE pad; geen try-catch rond database schrijven).
 * Nu: elke foutuitweg produceert een gecontroleerde error-state.
 */
class FormPerusahaanViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = PerusahaanRepository(application)

    private val _uiState = MutableStateFlow(FormUiState())
    val uiState: StateFlow<FormUiState> = _uiState.asStateFlow()

    fun loadForEdit(id: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null)
            try {
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
                        tahunPajakText = p.tahunPajak.toString(),
                        logoPath = p.logoPath,
                        isEdit = true,
                        loading = false,
                        daftarInduk = loadIndukOptions(excludeId = p.id),
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        error = "Perusahaan tidak ditemui. Data mungkin sudah dihapus.",
                    )
                }
            } catch (e: Exception) {
                Log.e("FormPerusahaan", "loadForEdit failed", e)
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = "Gagal memuat data: ${friendlyError(e)}",
                )
            }
        }
    }

    fun loadDaftarInduk() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(daftarInduk = loadIndukOptions(excludeId = _uiState.value.id))
            } catch (e: Exception) {
                Log.e("FormPerusahaan", "loadDaftarInduk failed", e)
                _uiState.value = _uiState.value.copy(error = "Gagal memuat daftar induk: ${friendlyError(e)}")
            }
        }
    }

    private suspend fun loadIndukOptions(excludeId: Long): List<Perusahaan> =
        repo.getAll().filter { it.id != excludeId && it.status == Perusahaan.STATUS_INDUK }

    fun updateNama(v: String) { _uiState.value = _uiState.value.copy(nama = v, saved = false) }
    fun updateNpwp(v: String) { _uiState.value = _uiState.value.copy(npwp = v, saved = false) }
    fun updateAlamat(v: String) { _uiState.value = _uiState.value.copy(alamat = v) }
    fun updateNegara(v: String) { _uiState.value = _uiState.value.copy(negara = v) }
    fun updateStatus(v: String) { _uiState.value = _uiState.value.copy(status = v) }
    fun updateParentId(v: Long?) { _uiState.value = _uiState.value.copy(parentId = v) }
    fun updateTahunPajak(v: String) { _uiState.value = _uiState.value.copy(tahunPajakText = v, saved = false) }
    fun updateLogoPath(v: String?) { _uiState.value = _uiState.value.copy(logoPath = v) }

    /** Validasi (sync, senza DB) — error inline per field, geen crash. */
    fun validateNow(): List<FormFieldError> {
        val s = _uiState.value
        return FormValidation.validatePerusahaan(
            nama = s.nama,
            npwp = s.npwp,
            status = s.status,
            tahunPajakText = s.tahunPajakText,
        )
    }

    /** Alur simpan: validasi -> duplikat-check -> DB insert/update, alles terkontrol. */
    fun save() {
        val s = _uiState.value
        val errors = validateNow()
        if (errors.isNotEmpty()) {
            // Terkontrol: tampil error inline, JANGAN crash
            _uiState.value = _uiState.value.copy(
                fieldErrors = errors.associate { it.field to it.message },
                error = "Pastikan form lengkap dan valid.",
                saved = false,
            )
            return
        }
        _uiState.value = _uiState.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val normalizedNpwp = s.npwp.trim().ifBlank { Perusahaan.EMPTY_NPWP }
                val existing = repo.getAll()
                val duplicate = FormValidation.findDuplicateNpwp(
                    npwp = normalizedNpwp,
                    excludeId = if (s.isEdit) s.id else 0,
                    existing = existing,
                )
                if (duplicate != null) {
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        saved = false,
                        error = "NPWP duplikat: data niet opgeslagen.",
                        fieldErrors = mapOf(
                            FormFieldError.FIELD_NPWP to "NPWP al gebruikt door ${duplicate.nama}",
                        ),
                    )
                    return@launch
                }

                val p = Perusahaan(
                    id = s.id,
                    nama = s.nama.trim(),
                    npwp = normalizedNpwp,
                    alamat = s.alamat.trim(),
                    negara = s.negara.trim().ifBlank { "Indonesia" },
                    status = s.status,
                    parentId = s.parentId,
                    tahunPajak = s.tahunPajakText.toIntOrNull() ?: 2024,
                    logoPath = s.logoPath,
                )
                try {
                    if (s.isEdit) repo.update(p) else repo.insert(p)
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        saved = true,
                        error = null,
                        fieldErrors = emptyMap(),
                    )
                } catch (db: Exception) {
                    Log.e("FormPerusahaan", "simpan perusahaan DB failed", db)
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        saved = false,
                        error = "Gagal simpan ke database: ${friendlyError(db)}",
                        fieldErrors = emptyMap(),
                    )
                }
            } catch (e: Exception) {
                Log.e("FormPerusahaan", "save failed (buiten DB)", e)
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    saved = false,
                    error = "Gagal simpan perusahaan: ${friendlyError(e)}",
                )
            }
        }
    }

    /** Bericht ramah user (geen stack trace). */
    private fun friendlyError(e: Exception): String =
        (e.message ?: "error onbekend").split('\n').first().take(120)
}