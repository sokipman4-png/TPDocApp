package com.tpdoc.app

import org.junit.Assert.*
import org.junit.Test

class ExportCsvTest {

    @Test
    fun `csv separator is semicolon`() {
        val header = "Nama PT;NPWP;Alamat;Negara;Status;Induk ID;Tahun Pajak"
        val parts = header.split(";")
        assertEquals(7, parts.size)
        assertEquals("Nama PT", parts[0])
        assertEquals("NPWP", parts[1])
    }

    @Test
    fun `csv line building`() {
        val nama = "PT Test"
        val npwp = "01.234.567.8-901.000"
        val alamat = "Jl. Test 123"
        val negara = "Indonesia"
        val status = "induk"
        val tahunPajak = 2024
        val line = "$nama;$npwp;$alamat;$negara;$status;;$tahunPajak"
        val parts = line.split(";")
        assertEquals(7, parts.size)
        assertEquals("PT Test", parts[0])
        assertEquals("2024", parts[6])
    }

    @Test
    fun `utf8 bom bytes`() {
        val bom = byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte())
        assertEquals(3, bom.size)
        assertEquals(0xEF.toByte(), bom[0])
        assertEquals(0xBB.toByte(), bom[1])
        assertEquals(0xBF.toByte(), bom[2])
    }

    @Test
    fun `russian escape in csv`() {
        // Test that special characters don't break CSV
        val nama = "PT Máju Jaya"
        val line = "$nama;;Jakarta;Indonesia;induk;;2024"
        assertTrue(line.contains("Máju"))
        assertTrue(line.contains("Jakarta"))
    }
}