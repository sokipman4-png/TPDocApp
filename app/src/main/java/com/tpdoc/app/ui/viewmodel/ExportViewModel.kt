package com.tpdoc.app.ui.viewmodel

import androidx.activity.ActivityResultContracts
import androidx.core.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.unit.Unit
import android.app.Application
import com.tpdoc.app.data.export.CsvCodec
import com.tpdoc.app.data.export.ExportUtils
import com.tpdoc.app.data.export.JsonCodec
import com.tpdoc.app.data.export.LogoValidator
import com.tpdoc.app.data.room.Perusahaan
import com.tpdoc.app.data.room.PerusahaanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

data class ExportUiState(
    val statusMessage: String? = null,
    val error: String? = null,
    val busy: Boolean = false,
    val showRestoreConfirm: Boolean = false,
    val pendingRestore: List<Perusahaan> = emptyList(),
)

/**
 * ViewModel export/backup/restore/share.
 * Export via SAF (ActivityResultContracts.CreateDocument/OpenDocument);
 * share via FileProvider + Intent.ACTION_SEND.
 */
class ExportViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = PerusahaanRepository(application)
    private val _uiState = MutableStateFlow(ExportUiState())
    val uiState: StateFlow<ExportUiState> = _uiState.asStateFlow()

    // ---- Export via SAF ----

    fun exportCsv() {
        viewModelScope.launch {
            setBusy()
            try {
                val all = repo.getAll()
                saveViaSaf("tpdoc_perusahaan.csv", "text/csv", CsvCodec.buildCsv(all))
                notifyOk("CSV eksporteri sukses.")
            } catch (e: Exception) {
                notifyError("Gagal export CSV: ${e.message}")
            } finally {
                clearBusy()
            }
        }
    }

    fun exportPdf() {
        viewModelScope.launch {
            setBusy()
            try {
                val all = repo.getAll()
                saveViaSaf("tpdoc_laporan_grup.pdf", "application/pdf", ExportUtils.pdfBytes(all))
                notifyOk("PDF grup eksporteri sukses.")
            } catch (e: Exception) {
                notifyError("Gagal export PDF: ${e.message}")
            } finally {
                clearBusy()
            }
        }
    }

    fun exportPdfOne(perusahaan: Perusahaan) {
        viewModelScope.launch {
            setBusy()
            try {
                saveViaSaf("tpdoc_${perusahaan.nama.slugify()}.pdf", "application/pdf", ExportUtils.pdfBytesOne(perusahaan))
                notifyOk("PDF perusahaan eksporteri sukses.")
            } catch (e: Exception) {
                notifyError("Gagal export PDF perusahaan: ${e.message}")
            } finally {
                clearBusy()
            }
        }
    }

    // ---- Backup (JSON) ----

    fun backupJson() {
        viewModelScope.launch {
            setBusy()
            try {
                val all = repo.getAll()
                saveViaSaf("tpdoc_backup.json", "application/json", JsonCodec.encode(all))
                notifyOk("Backup JSON eksporteri sukses.")
            } catch (e: Exception) {
                notifyError("Gagal backup JSON: ${e.message}")
            } finally {
                clearBusy()
            }
        }
    }

    // ---- Restore ----

    fun pickRestoreFile() {
        viewModelScope.launch {
            setBusy()
            try {
                val uri = application.startActivityForResult(
                    ActivityResultContracts.OpenDocument.withType("application/json"),
                    Unit.Default,
                ).requireSuccess().getOrThrow()

                val jsonText = uri.getContentHub().use { hub ->
                    hub.readText("application/json")
                }
                val restored = JsonCodec.decode(jsonText)
                if (restored.isEmpty()) {
                    notifyError("File backup kosong atau tidak valid.")
                } else {
                    _uiState.value = _uiState.value.copy(
                        pendingRestore = restored,
                        showRestoreConfirm = true,
                    )
                }
            } catch (e: Exception) {
                notifyError("Gagal baca file backup: ${e.message}")
            } finally {
                clearBusy()
            }
        }
    }

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
                val b = CsvCodec.buildCsvBytes(all)
                val file = writeCacheFile("share", "tpdoc_perusahaan.csv", b)
                ExportUtils.shareFile(application, file, "text/csv", "Deel CSV")
            } catch (e: Exception) {
                notifyError("Gagal share CSV: ${e.message}")
            }
        }
    }

    fun sharePerusahaan(perusahaan: Perusahaan) {
        viewModelScope.launch {
            try {
                val b = CsvCodec.buildCsvBytes(listOf(perusahaan))
                val file = writeCacheFile("share", "tpdoc_${perusahaan.nama.slugify()}.csv", b)
                ExportUtils.shareFile(application, file, "text/csv", "Deel Profil ${perusahaan.nama}")
            } catch (e: Exception) {
                notifyError("Gagal share perusahaan: ${e.message}")
            }
        }
    }

    // ---- Logo upload ----

    /** Pilih file logo via SAF GetContent, validasi, simpan, dan update perusahaan. */
    fun pickLogo(perusahaanId: Long) {
        viewModelScope.launch {
            try {
                val uri = application.startActivityForResult(
                    ActivityResultContracts.GetContent,
                    Unit.Default,
                ).requireSuccess().getOrThrow()

                val mime = uri.getMimeType()
                if (mime == null || mime.isBlank()) {
                    notifyError("File tidak memiliki tipe. Pilih JPG/PNG.")
                    return@launch
                }
                if (!LogoValidator.isValidMime(mime)) {
                    notifyError("Format file tidak didukung. Hanya JPG dan PNG.")
                    return@launch
                }
                val bytes = uri.getContentHub().use { hub -> hub.read(mime) }
                val validationError = LogoValidator.validate(mime, bytes.size.toLong())
                if (validationError != null) {
                    notifyError(validationError)
                    return@launch
                }
                val ext = if (mime.lowercase() == "image/png") "png" else "jpg"
                val file = writeCacheFile("logos", "logo_${perusahaanId}_${System.currentTimeMillis()}.$ext", bytes)
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

    private suspend fun saveViaSaf(suggestedName: String, mimeType: String, text: String) {
        val contract = ActivityResultContracts.CreateDocument.withName(suggestedName).withType(mimeType)
        val uri = application.startActivityForResult(contract, Unit.Default).requireSuccess().getOrThrow()
        uri.getContentHub(Uri.Mode.Append).use { hub -> hub.writeText(mimeType, text) }
    }

    private suspend fun saveViaSaf(suggestedName: String, mimeType: String, bytes: ByteArray) {
        val contract = ActivityResultContracts.CreateDocument.withName(suggestedName).withType(mimeType)
        val uri = application.startActivityForResult(contract, Unit.Default).requireSuccess().getOrThrow()
        uri.getContentHub(Uri.Mode.Append).use { hub -> hub.write(mimeType, bytes) }
    }

    private fun writeCacheFile(dirName: String, fileName: String, bytes: ByteArray): File {
        val dir = File(application.cacheDir, dirName)
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