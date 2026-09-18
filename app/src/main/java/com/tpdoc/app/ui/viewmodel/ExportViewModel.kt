package com.tpdoc.app.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tpdoc.app.data.export.CsvCodec
import com.tpdoc.app.data.export.ExportUtils
import com.tpdoc.app.data.export.JsonCodec
import com.tpdoc.app.data.export.LogoValidator
import com.tpdoc.app.data.room.Perusahaan
import com.tpdoc.app.data.room.PerusahaanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

data class ExportUiState(
    val statusMessage: String? = null,
    val error: String? = null,
    val busy: Boolean = false,
    val showRestoreConfirm: Boolean = false,
    val pendingRestore: List<Perusahaan> = emptyList(),
)

/**
 * State machine restore yang murni (tanpa Android/Room) — unit-testable di JVM.
 *
 * Kontrak alur restore:
 * 1. Memilih file backup HANYA menghasilkan state "tampilkan dialog konfirmasi".
 * 2. Import data (replaceAll) hanya terjadi lewat [ExportViewModel.confirmRestore],
 *    yang menolak jalan jika pendingRestore kosong.
 * Jadi dialog konfirmasi SELALU muncul sebelum data existing ditimpa.
 */
internal object RestoreFlow {

    fun onBackupPicked(jsonText: String): ExportUiState {
        if (jsonText.isBlank()) {
            return ExportUiState(error = "File backup kosong atau tidak valid.")
        }
        return try {
            val restored = JsonCodec.decode(jsonText)
            if (restored.isEmpty()) {
                ExportUiState(error = "File backup kosong atau tidak valid.")
            } else {
                ExportUiState(showRestoreConfirm = true, pendingRestore = restored)
            }
        } catch (e: Exception) {
            ExportUiState(error = "Gagal membaca file backup: ${e.message}")
        }
    }
}

/**
 * ViewModel export/backup/restore/share.
 *
 * - Export & backup: tulis ke URI hasil SAF CreateDocument (launcher di screen);
 *   tulis lewat ContentResolver.openOutputStream — API Android standar.
 * - Restore: baca URI hasil SAF OpenDocument → parse JSON → dialog konfirmasi → import.
 * - Share: FileProvider + Intent.ACTION_SEND (file di cacheDir/share).
 * - Logo: baca + validasi (JPG/PNG max 5MB) + simpan ke cacheDir/logos.
 */
class ExportViewModel(application: Application) : AndroidViewModel(application) {

    private val ctx: Context = application
    private val app: Application = application
    private val repo = PerusahaanRepository(application)
    private val _uiState = MutableStateFlow(ExportUiState())
    val uiState: StateFlow<ExportUiState> = _uiState.asStateFlow()

    // ---- Export via SAF ----

    fun exportCsvTo(uri: Uri) {
        viewModelScope.launch {
            setBusy()
            try {
                val bytes = withContext(Dispatchers.IO) { CsvCodec.buildCsvBytes(repo.getAll()) }
                writeToUri(uri, bytes)
                notifyOk("CSV terexport sukses.")
            } catch (e: Exception) {
                notifyError("Gagal export CSV: ${e.message}")
            } finally {
                clearBusy()
            }
        }
    }

    fun exportPdfTo(uri: Uri) {
        viewModelScope.launch {
            setBusy()
            try {
                val bytes = withContext(Dispatchers.IO) { ExportUtils.pdfBytes(repo.getAll()) }
                writeToUri(uri, bytes)
                notifyOk("PDF grup terexport sukses.")
            } catch (e: Exception) {
                notifyError("Gagal export PDF: ${e.message}")
            } finally {
                clearBusy()
            }
        }
    }

    /** PDF satu perusahaan (untuk tombol "Export PDF Perusahaan"). */
    fun exportPdfOneTo(uri: Uri, perusahaan: Perusahaan) {
        viewModelScope.launch {
            setBusy()
            try {
                val bytes = withContext(Dispatchers.IO) { ExportUtils.pdfBytesOne(perusahaan) }
                writeToUri(uri, bytes)
                notifyOk("PDF perusahaan terexport sukses.")
            } catch (e: Exception) {
                notifyError("Gagal export PDF perusahaan: ${e.message}")
            } finally {
                clearBusy()
            }
        }
    }

    fun backupJsonTo(uri: Uri) {
        viewModelScope.launch {
            setBusy()
            try {
                val text = withContext(Dispatchers.IO) { JsonCodec.encode(repo.getAll()) }
                writeToUri(uri, text.toByteArray())
                notifyOk("Backup JSON terexport sukses.")
            } catch (e: Exception) {
                notifyError("Gagal backup JSON: ${e.message}")
            } finally {
                clearBusy()
            }
        }
    }

    // ---- Restore ----

    /** Baca file backup dari URI SAF, parse, lalu tampilkan dialog konfirmasi. */
    fun restoreFrom(uri: Uri) {
        viewModelScope.launch {
            setBusy()
            try {
                val jsonText = readFromUri(uri)
                _uiState.value = RestoreFlow.onBackupPicked(jsonText)
            } catch (e: Exception) {
                notifyError("Gagal baca file backup: ${e.message}")
            } finally {
                // Menjaga busy=false bila state baru tidak meng-overwrite-nya
                if (_uiState.value.busy) clearBusy()
            }
        }
    }

    /** Satu-satunya jalur import: mengganti seluruh data. Ditolak jika tidak ada pending. */
    fun confirmRestore() {
        viewModelScope.launch {
            val pending = _uiState.value.pendingRestore
            if (pending.isEmpty()) return@launch
            _uiState.value = _uiState.value.copy(showRestoreConfirm = false, busy = true)
            try {
                repo.replaceAll(pending)
                notifyOk("Restore selesai: ${pending.size} perusahaan terimport.")
            } catch (e: Exception) {
                notifyError("Gagal restore: ${e.message}")
            } finally {
                clearBusy()
            }
        }
    }

    fun cancelRestore() {
        _uiState.value = _uiState.value.copy(showRestoreConfirm = false, pendingRestore = emptyList())
    }

    // ---- Share ----

    fun shareCsv() {
        viewModelScope.launch {
            try {
                val all = repo.getAll()
                val bytes = withContext(Dispatchers.IO) { CsvCodec.buildCsvBytes(all) }
                val file = withContext(Dispatchers.IO) {
                    writeCacheFile("share", "tpdoc_perusahaan.csv", bytes)
                }
                ExportUtils.shareFile(ctx, file, "text/csv", "Share CSV")
            } catch (e: Exception) {
                notifyError("Gagal share CSV: ${e.message}")
            }
        }
    }

    fun sharePerusahaan(perusahaan: Perusahaan) {
        viewModelScope.launch {
            try {
                val bytes = withContext(Dispatchers.IO) { CsvCodec.buildCsvBytes(listOf(perusahaan)) }
                val file = withContext(Dispatchers.IO) {
                    writeCacheFile("share", "tpdoc_${perusahaan.nama.slugify()}.csv", bytes)
                }
                ExportUtils.shareFile(ctx, file, "text/csv", "Share Profil ${perusahaan.nama}")
            } catch (e: Exception) {
                notifyError("Gagal share perusahaan: ${e.message}")
            }
        }
    }

    // ---- Logo upload ----

    /** Baca logo dari URI SAF GetContent, validasi format+ukuran, simpan, update perusahaan. */
    fun saveLogoFromUri(perusahaanId: Long, uri: Uri) {
        viewModelScope.launch {
            try {
                val resolver = ctx.contentResolver
                val mime = resolver.getType(uri)
                if (mime == null || mime.isBlank()) {
                    notifyError("File tidak memiliki tipe. Pilih file JPG atau PNG.")
                    return@launch
                }
                val bytes = withContext(Dispatchers.IO) {
                    val input = resolver.openInputStream(uri) ?: throw IOException("Tidak bisa membaca file logo")
                    input.use { it.readBytes() }
                }
                val validationError = LogoValidator.validate(mime, bytes.size.toLong())
                if (validationError != null) {
                    notifyError(validationError)
                    return@launch
                }
                val ext = if (mime.lowercase() == "image/png") "png" else "jpg"
                val file = withContext(Dispatchers.IO) {
                    writeCacheFile("logos", "logo_${perusahaanId}_${System.currentTimeMillis()}.$ext", bytes)
                }
                val p = repo.getById(perusahaanId) ?: return@launch
                repo.update(p.copy(logoPath = file.path))
                notifyOk("Logo terupload sukses.")
            } catch (e: Exception) {
                notifyError("Gagal upload logo: ${e.message}")
            }
        }
    }

    fun clearStatus() {
        _uiState.value = _uiState.value.copy(statusMessage = null, error = null)
    }

    // ---- Helpers ----

    private suspend fun writeToUri(uri: Uri, bytes: ByteArray) {
        withContext(Dispatchers.IO) {
            val out = ctx.contentResolver.openOutputStream(uri)
                ?: throw IOException("Tidak bisa membuka file tujuan")
            out.use { it.write(bytes) }
        }
    }

    private suspend fun readFromUri(uri: Uri): String {
        return withContext(Dispatchers.IO) {
            val input = ctx.contentResolver.openInputStream(uri)
                ?: throw IOException("Tidak bisa membuka file sumber")
            input.bufferedReader().use { it.readText() }
        }
    }

    private fun writeCacheFile(dirName: String, fileName: String, bytes: ByteArray): File {
        val dir = File(app.cacheDir, dirName)
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, fileName)
        file.outputStream().use { out -> out.write(bytes) }
        return file
    }

    private fun setBusy() {
        _uiState.value = _uiState.value.copy(busy = true, error = null, statusMessage = null)
    }

    private fun clearBusy() {
        _uiState.value = _uiState.value.copy(busy = false)
    }

    private fun notifyOk(message: String) {
        _uiState.value = _uiState.value.copy(statusMessage = message, error = null)
    }

    private fun notifyError(message: String) {
        _uiState.value = _uiState.value.copy(error = message, statusMessage = null)
    }
}

private fun String.slugify(): String {
    val sb = StringBuilder()
    forEach { c ->
        sb.append(if (c.isLetterOrDigit() || c == '-') c else '-')
    }
    return sb.toString().trim('-').ifBlank { "perusahaan" }
}