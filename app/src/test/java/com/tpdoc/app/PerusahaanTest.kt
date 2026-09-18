package com.tpdoc.app

import com.tpdoc.app.data.room.Perusahaan
import org.junit.Assert.*
import org.junit.Test

class PerusahaanTest {

    @Test
    fun `perusahaan entity default values`() {
        val p = Perusahaan(nama = "PT Test", npwp = "01.234.567.8-901.000")
        assertEquals("PT Test", p.nama)
        assertEquals("01.234.567.8-901.000", p.npwp)
        assertEquals("", p.alamat)
        assertEquals("Indonesia", p.negara)
        assertEquals(Perusahaan.STATUS_INDUK, p.status)
        assertNull(p.parentId)
        assertEquals(2024, p.tahunPajak)
        assertNull(p.logoPath)
    }

    @Test
    fun `perusahaan status constants`() {
        assertEquals("induk", Perusahaan.STATUS_INDUK)
        assertEquals("anak", Perusahaan.STATUS_ANAK)
        assertEquals("cucu", Perusahaan.STATUS_CUCU)
        assertEquals("cabang", Perusahaan.STATUS_CABANG)
        assertEquals(4, Perusahaan.STATUSES.size)
    }

    @Test
    fun `perusahaan copy for duplicate`() {
        val original = Perusahaan(
            id = 1,
            nama = "PT Original",
            npwp = "00.000.000.0-000.000",
            alamat = "Jl. Test",
            negara = "Indonesia",
            status = Perusahaan.STATUS_INDUK,
            tahunPajak = 2024,
        )
        val copy = original.copy(
            id = 0,
            nama = original.nama + " - Copy",
            npwp = "-",
        )
        assertEquals(0, copy.id)
        assertEquals("PT Original - Copy", copy.nama)
        assertEquals("-", copy.npwp)
        assertEquals(original.alamat, copy.alamat)
        assertEquals(original.negara, copy.negara)
        assertEquals(original.status, copy.status)
        assertEquals(original.tahunPajak, copy.tahunPajak)
        assertEquals(original.logoPath, copy.logoPath)
    }

    @Test
    fun `perusahaan hierarchy parentId nullable`() {
        val induk = Perusahaan(nama = "Induk", npwp = "1")
        val anak = Perusahaan(nama = "Anak", npwp = "2", parentId = 1L, status = Perusahaan.STATUS_ANAK)
        assertNull(induk.parentId)
        assertEquals(1L, anak.parentId)
    }
}