package com.tpdoc.app.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tpdoc.app.data.dummy.DummyDataFactory
import com.tpdoc.app.data.room.PerusahaanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * UiState tombol data contoh (STEP 4 Tahap 2.6).
 */
data class DummyUiState(
    val snackbar: String? = null,
    val error: String? = null,
    val busy: Boolean = false,
    val showConfirm: Boolean = false,
    val hasDummy: Boolean = false,
)

/**
 * ViewModel "Muat Data Contoh (Dummy)".
 *
 * Alur (STEP 4):
 *  1. Dialog konfirmasi: "Data contoh akan ditambahkan. Lanjut? Data existing niet verwijderd."
 *  2. Generate 3 bedrijven in hiërarchie (induk-anak-cucu) x 2 belastingjaren (2024/2025),
 *     elk met isDummy=true zodat het nooit met echte userdata wordt gemengd.
 *  3. Logo placeholder (BMP kleurvlak) naar internal storage cacheDir/dummy_logos.
 *  4. Snackbar: "Data contoh berhasil dibuat: 3 perusahaan."
 *  5. "Hapus Data Contoh" verwijdert alleen isDummy=true rijen.
 */
class DummyDataViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = PerusahaanRepository(application)
    private val app: Application = application

    private val _uiState = MutableStateFlow(DummyUiState())
    val uiState: StateFlow<DummyUiState> = _uiState.asStateFlow()

    /** Laad hasDummy status (voor het tonen van de opschoonknop). */
    fun refreshHasDummy() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(hasDummy = repo.getDummy().isNotEmpty())
            } catch (e: Exception) {
                Log.e("DummyData", "refreshHasDummy failed", e)
            }
        }
    }

    fun askGenerate() {
        _uiState.value = _uiState.value.copy(showConfirm = true, error = null)
    }

    fun cancelGenerate() {
        _uiState.value = _uiState.value.copy(showConfirm = false)
    }

    fun generate(onDone: (() -> Unit)? = null) {
        _uiState.value = _uiState.value.copy(showConfirm = false, busy = true, error = null)
        viewModelScope.launch {
            try {
                val rows = DummyDataFactory.build()
                val idByKey = HashMap<String, Long>()
                val inserted = ArrayList<com.tpdoc.app.data.room.Perusahaan>()
                for (row in rows) {
                    val parentId = row.parentKey?.let { idByKey[it] }
                    val id = repo.insert(row.perusahaan.copy(id = 0, parentId = parentId))
                    idByKey[row.key] = id
                    inserted.add(row.perusahaan.copy(id = id, parentId = parentId))
                }

                // Logo placeholder: BMP klein kleurvlak -> cacheDir/dummy_logos (optioneel).
                try {
                    withContext(Dispatchers.IO) {
                        val dir = File(app.cacheDir, "dummy_logos")
                        if (!dir.exists()) dir.mkdirs()
                        for (p in inserted) {
                            val bmp = DummyDataFactory.logoBmp(p.nama)
                            val slug = p.nama.lowercase().replace(" ", "_").replace(".", "")
                            val f = File(dir, "logo_$slug.bmp")
                            f.writeBytes(bmp)
                            repo.update(p.copy(logoPath = f.path))
                        }
                    }
                } catch (logoErr: Exception) {
                    // Logo-falen mag de dummy-data niet blokkeren.
                    Log.e("DummyData", "logo placeholder skip", logoErr)
                }

                _uiState.value = _uiState.value.copy(
                    busy = false,
                    hasDummy = true,
                    snackbar = "Data contoh berhasil dibuat: 3 perusahaan (${inserted.size} record, 2 tahun pajak).",
                    error = null,
                )
                onDone?.invoke()
            } catch (e: Exception) {
                Log.e("DummyData", "generate failed", e)
                _uiState.value = _uiState.value.copy(
                    busy = false,
                    error = "Gagal membuat data contoh: ${(e.message ?: "error onbekend").take(120)}",
                )
            }
        }
    }

    fun askDeleteDummy() {
        _uiState.value = _uiState.value.copy(showConfirm = true, error = null)
    }

    fun deleteDummy() {
        _uiState.value = _uiState.value.copy(showConfirm = false, busy = true, error = null)
        viewModelScope.launch {
            try {
                repo.deleteDummy()
                _uiState.value = _uiState.value.copy(
                    busy = false,
                    hasDummy = false,
                    snackbar = "Data contoh dihapus.",
                    error = null,
                )
            } catch (e: Exception) {
                Log.e("DummyData", "deleteDummy failed", e)
                _uiState.value = _uiState.value.copy(
                    busy = false,
                    error = "Gagal hapus data contoh: ${(e.message ?: "error onbekend").take(120)}",
                )
            }
        }
    }

    fun dismiss() {
        _uiState.value = _uiState.value.copy(snackbar = null, error = null, showConfirm = false)
    }
}