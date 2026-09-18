package com.tpdoc.app

import com.tpdoc.app.calc.BerelasiChecker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Logika checklist pihak berelasi: minimal satu kriteria terpenuhi -> berelasi. */
class BerelasiCheckerTest {

    @Test
    fun `tidak ada kriteria terpenuhi - tidak berelasi`() {
        assertFalse(BerelasiChecker.terindikasiBerelasi(listOf(false, false, false, false)))
        assertFalse(BerelasiChecker.terindikasiBerelasi(emptyList()))
    }

    @Test
    fun `satu kriteria terpenuhi - berelasi`() {
        assertTrue(BerelasiChecker.terindikasiBerelasi(listOf(true, false, false, false)))
        assertTrue(BerelasiChecker.terindikasiBerelasi(listOf(false, true, false, false)))
        assertTrue(BerelasiChecker.terindikasiBerelasi(listOf(false, false, false, true)))
    }

    @Test
    fun `semua kriteria terpenuhi - berelasi`() {
        assertTrue(BerelasiChecker.terindikasiBerelasi(listOf(true, true, true, true)))
    }

    @Test
    fun `jumlah terpenuhi dihitung benar`() {
        assertEquals(0, BerelasiChecker.jumlahTerpenuhi(listOf(false, false)))
        assertEquals(2, BerelasiChecker.jumlahTerpenuhi(listOf(true, false, true)))
    }

    @Test
    fun `daftar kriteria standar berjumlah 4 dan memuat kepemilikan 25 persen`() {
        assertEquals(4, BerelasiChecker.KRITERIA.size)
        assertTrue(BerelasiChecker.KRITERIA.any { it.judul.contains("25%") })
    }
}