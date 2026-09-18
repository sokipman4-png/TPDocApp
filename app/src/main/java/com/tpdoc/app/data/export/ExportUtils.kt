package com.tpdoc.app.data.export

import android.content.Context
import android.content.Intent
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.tpdoc.app.data.room.Perusahaan
import java.io.File
import java.io.FileOutputStream

object ExportUtils {

    fun exportCsv(perusahaans: List<Perusahaan>, outputDir: File): File {
        val file = File(outputDir, "tpdoc_perusahaan_${System.currentTimeMillis()}.csv")
        // UTF-8 BOM for Excel Indonesia compatibility
        val bom = byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte())
        file.outputStream().use { out ->
            out.write(bom)
            val writer = out.bufferedWriter()
            // Header with semicolon separator
            writer.write("Nama PT;NPWP;Alamat;Negara;Status;Induk ID;Tahun Pajak")
            writer.newLine()
            perusahaans.forEach { p ->
                writer.write("${p.nama};${p.npwp};${p.alamat};${p.negara};${p.status};${p.parentId ?: ""};${p.tahunPajak}")
                writer.newLine()
            }
            writer.flush()
        }
        return file
    }

    fun exportPdf(perusahaans: List<Perusahaan>, outputDir: File): File {
        val file = File(outputDir, "tpdoc_laporan_${System.currentTimeMillis()}.pdf")
        val document = PdfDocument()

        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
        var page = document.startPage(pageInfo)
        val canvas = page.canvas
        var y = 50f

        val titlePaint = android.graphics.Paint().apply {
            textSize = 18f
            isFakeBoldText = true
            color = android.graphics.Color.BLACK
        }
        val bodyPaint = android.graphics.Paint().apply {
            textSize = 10f
            color = android.graphics.Color.BLACK
        }
        val lineHeight = 20f

        canvas.drawText("Laporan TP Doc - Ringkasan Grup", 40f, y, titlePaint)
        y += 30f

        perusahaans.forEach { p ->
            if (y > 800f) {
                document.finishPage(page)
                page = document.startPage(pageInfo)
                y = 50f
            }
            canvas.drawText("${p.nama} (${p.status})", 40f, y, bodyPaint)
            y += lineHeight
            canvas.drawText("  NPWP: ${p.npwp}  |  Negera: ${p.negara}  |  Tahum: ${p.tahunPajak}", 40f, y, bodyPaint)
            y += lineHeight
            canvas.drawText("  Alamat: ${p.alamat.ifBlank { "-" }}", 40f, y, bodyPaint)
            y += lineHeight + 10f
        }

        document.finishPage(page)
        FileOutputStream(file).use { document.writeTo(it) }
        document.close()
        return file
    }

    fun exportJson(perusahaans: List<Perusahaan>, outputDir: File): File {
        val file = File(outputDir, "tpdoc_backup_${System.currentTimeMillis()}.json")
        val sb = StringBuilder()
        sb.appendLine("[")
        perusahaans.forEachIndexed { i, p ->
            sb.appendLine("  {")
            sb.appendLine("    \"id\": ${p.id},")
            sb.appendLine("    \"nama\": \"${p.nama}\",")
            sb.appendLine("    \"npwp\": \"${p.npwp}\",")
            sb.appendLine("    \"alamat\": \"${p.alamat}\",")
            sb.appendLine("    \"negara\": \"${p.negara}\",")
            sb.appendLine("    \"status\": \"${p.status}\",")
            sb.appendLine("    \"parentId\": ${p.parentId},")
            sb.appendLine("    \"tahunPajak\": ${p.tahunPajak},")
            sb.appendLine("    \"logoPath\": ${if (p.logoPath != null) "\"${p.logoPath}\"" else "null"}")
            sb.append("  }")
            if (i < perusahaans.size - 1) sb.appendLine(",") else sb.appendLine()
        }
        sb.appendLine("]")
        file.writeText(sb.toString())
        return file
    }

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