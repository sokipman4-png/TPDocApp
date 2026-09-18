package com.tpdoc.app.data.export

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.tpdoc.app.data.room.Perusahaan
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object ExportUtils {

    // ---- CSV ----

    fun exportCsv(perusahaans: List<Perusahaan>, outputDir: File): File {
        val file = File(outputDir, "tpdoc_perusahaan_${System.currentTimeMillis()}.csv")
        file.writeBytes(CsvCodec.buildCsvBytes(perusahaans))
        return file
    }

    // ---- PDF ----

    fun exportPdf(perusahaans: List<Perusahaan>, outputDir: File): File {
        val file = File(outputDir, "tpdoc_laporan_${System.currentTimeMillis()}.pdf")
        FileOutputStream(file).use { it.write(pdfBytes(perusahaans)) }
        return file
    }

    /** Ringkasan satu perusahaan (detail). */
    fun pdfBytesOne(perusahaan: Perusahaan): ByteArray {
        return pdfBytes(listOf(perusahaan), title = "Detail Perusahaan - ${perusahaan.nama}")
    }

    fun pdfBytes(perusahaans: List<Perusahaan>, title: String = "Laporan TP Doc - Ringkasan Grup"): ByteArray {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4

        val titlePaint = Paint().apply {
            textSize = 18f
            isFakeBoldText = true
            color = android.graphics.Color.BLACK
        }
        val bodyPaint = Paint().apply { textSize = 10f; color = android.graphics.Color.BLACK }
        val lineHeight = 20f

        var page = document.startPage(pageInfo)
        val canvas = page.canvas
        var y = 50f

        canvas.drawText(title, 40f, y, titlePaint)
        y += 30f

        perusahaans.forEach { p ->
            if (y > 800f) {
                document.finishPage(page)
                page = document.startPage(pageInfo)
                y = 50f
            }
            canvas.drawText("${p.nama} (${p.status})", 40f, y, bodyPaint)
            y += lineHeight
            canvas.drawText("  NPWP: ${p.npwp}  |  Negara: ${p.negara}  |  Tahun: ${p.tahunPajak}", 40f, y, bodyPaint)
            y += lineHeight
            canvas.drawText("  Alamat: ${p.alamat.ifBlank { "-" }}", 40f, y, bodyPaint)
            y += lineHeight + 10f
        }

        document.finishPage(page)
        val bos = ByteArrayOutputStream()
        document.writeTo(bos)
        document.close()
        return bos.toByteArray()
    }

    // ---- JSON backup ----

    fun exportJson(perusahaans: List<Perusahaan>, outputDir: File): File {
        val file = File(outputDir, "tpdoc_backup_${System.currentTimeMillis()}.json")
        file.writeText(JsonCodec.encode(perusahaans))
        return file
    }

    // ---- Share ----

    fun shareFile(context: Context, file: File, mimeType: String, title: String) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file,
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, title))
    }
}