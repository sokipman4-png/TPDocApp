package com.tpdoc.app

import com.tpdoc.app.data.export.CsvCodec
import com.tpdoc.app.data.room.Perusahaan
import org.junit.Assert.*
import org.junit.Test

class CsvCodecTest {

    private fun sample(): List<Perusahaan> = listOf(
        Perusahaan(id = 1, nama = "PT Alpha", npwp = "01.111.222.3-444.555", alamat = "Jl. Industri 5", negara = "Indonesia", status = Perusahaan.STATUS_INDUK, tahunPajak = 2024),
        Perusahaan(id = 2, nama = "PT Beta", npwp = "02.222.333.4-555.666", alamat = "Jl. Komers 9", negara = "Singapur", status = Perusahaan.STATUS_ANAK, parentId = 1L, tahunPajak = 2025),
    )

    @Test
    fun `csv header dengan semicolon separator`() {
        val csv = CsvCodec.buildCsv(sample())
        val lines = csv.split("\n").filter { it.isNotBlank() }
        val header = lines[0]
        val cols = header.split(";")
        assertEquals(8, cols.size)
        assertEquals("Nama PT", cols[0])
        assertEquals("Tahun Pajak", cols[6])
        assertEquals("Parent Nama", cols[7])
    }

    @Test
    fun `csv includes data dengan semicolon`() {
        val csv = CsvCodec.buildCsv(sample())
        val lines = csv.split("\n").filter { it.isNotBlank() }
        assertEquals(3, lines.size) // header + 2 perusahaan
        val row1 = lines[1].split(";")
        assertEquals("PT Alpha", row1[0])
        assertEquals("01.111.222.3-444.555", row1[1])
        assertEquals("induk", row1[4])
        assertEquals("", row1[5])
        assertEquals("2024", row1[6])
    }

    @Test
    fun `parent nama resolusi`() {
        val csv = CsvCodec.buildCsv(sample())
        val row2 = csv.split("\n").filter { it.isNotBlank() }[2].split(";")
        assertEquals("PT Alpha", row2[7])
    }

    @Test
    fun `utf8 bom present`() {
        val bytes = CsvCodec.buildCsvBytes(sample())
        assertEquals(0xEF.toByte(), bytes[0])
        assertEquals(0xBB.toByte(), bytes[1])
        assertEquals(0xBF.toByte(), bytes[2])
    }

    @Test
    fun `csv cell quotes semicolon-value`() {
        val p = Perusahaan(nama = "PT;Holding", npwp = "1", alamat = "Jl, A")
        val csv = CsvCodec.buildCsv(listOf(p))
        assertTrue(csv.contains("\"PT;Holding\""))
    }
}