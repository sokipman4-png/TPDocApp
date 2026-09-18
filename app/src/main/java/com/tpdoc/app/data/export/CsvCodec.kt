package com.tpdoc.app.data.export

import com.tpdoc.app.data.room.Perusahaan

/**
 * Builder CSV pure (separator ; + UTF-8 BOM). Unit-testable.
 */
object CsvCodec {

    const val SEPARATOR = ";"

    val BOM: ByteArray = byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte())

    fun buildCsv(perusahaans: List<Perusahaan>): String {
        val sb = StringBuilder()
        sb.appendLine("Nama PT;NPWP;Alamat;Negara;Status;Induk ID;Tahun Pajak;Parent Nama")
        val byId = perusahaans.associateBy { it.id }
        perusahaans.forEach { p ->
            val parentName = p.parentId?.let { byId[it]?.nama } ?: ""
            sb.appendLine(
                csvCell(p.nama) + SEPARATOR +
                csvCell(p.npwp) + SEPARATOR +
                csvCell(p.alamat) + SEPARATOR +
                csvCell(p.negara) + SEPARATOR +
                csvCell(p.status) + SEPARATOR +
                (p.parentId ?: "") + SEPARATOR +
                p.tahunPajak.toString() + SEPARATOR +
                csvCell(parentName)
            )
        }
        return sb.toString()
    }

    fun buildCsvBytes(perusahaans: List<Perusahaan>): ByteArray {
        val csv = buildCsv(perusahaans)
        return BOM + csv.toByteArray()
    }

    private fun csvCell(value: String): String {
        // Toklas nilai: quote sebagai cel jika mengandung separator, quote, atau newline
        val v = value.replace("\"", "\"\"")
        if (v.contains(SEPARATOR) || v.contains("\"") || v.contains("\n")) {
            return "\"$v\""
        }
        return v
    }
}