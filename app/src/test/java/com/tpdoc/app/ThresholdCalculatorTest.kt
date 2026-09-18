package com.tpdoc.app

import com.tpdoc.app.calc.ThresholdCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Test akurasi kalkulator threshold sesuai angka dokumen:
 *  - Master File + Local File: omzet grup > Rp50 M DAN transaksi afiliasi > Rp20 M
 *  - CbCR: omzet grup > Rp11 T
 *  - Batas strictly greater (>): tepat di threshold BELUM wajib.
 */
class ThresholdCalculatorTest {

    // ---------- Master File + Local File ----------

    @Test
    fun `omzet dan transaksi di atas threshold - wajib Master dan Local`() {
        val r = ThresholdCalculator.hitung(
            omzetGrup = 60_000_000_000L,       // > Rp50 M
            transaksiAfiliasi = 25_000_000_000L // > Rp20 M
        )
        assertTrue("harus wajib Master+Local", r.wajibMasterLocal)
    }

    @Test
    fun `omzet tepat 50 M - tidak wajib Master dan Local`() {
        val r = ThresholdCalculator.hitung(50_000_000_000L, 25_000_000_000L)
        assertFalse("tepat Rp50 M belum wajib (strictly greater)", r.wajibMasterLocal)
    }

    @Test
    fun `transaksi tepat 20 M - tidak wajib Master dan Local`() {
        val r = ThresholdCalculator.hitung(60_000_000_000L, 20_000_000_000L)
        assertFalse("tepat Rp20 M belum wajib (strictly greater)", r.wajibMasterLocal)
    }

    @Test
    fun `omzet besar tapi transaksi afiliasi kecil - tidak wajib dokumen`() {
        val r = ThresholdCalculator.hitung(500_000_000_000L, 5_000_000_000L)
        assertFalse("kondisi AND: transaksi kecil -> tidak wajib", r.wajibMasterLocal)
    }

    @Test
    fun `transaksi besar tapi omzet kecil - tidak wajib dokumen`() {
        val r = ThresholdCalculator.hitung(30_000_000_000L, 100_000_000_000L)
        assertFalse("kondisi AND: omzet kecil -> tidak wajib", r.wajibMasterLocal)
    }

    @Test
    fun `tanpa transaksi dan tanpa omzet - tidak wajib`() {
        val r = ThresholdCalculator.hitung(0L, 0L)
        assertFalse(r.wajibMasterLocal)
        assertFalse(r.wajibCbcr)
    }

    // ---------- CbCR ----------

    @Test
    fun `omzet di atas 11 T - wajib CbCR`() {
        val r = ThresholdCalculator.hitung(12_000_000_000_000L, 0L)
        assertTrue("omzet > Rp11 T wajib CbCR", r.wajibCbcr)
    }

    @Test
    fun `omzet tepat 11 T - tidak wajib CbCR`() {
        val r = ThresholdCalculator.hitung(11_000_000_000_000L, 0L)
        assertFalse("tepat Rp11 T belum wajib CbCR (strictly greater)", r.wajibCbcr)
    }

    @Test
    fun `omzet di bawah 11 T - tidak wajib CbCR`() {
        val r = ThresholdCalculator.hitung(10_000_000_000_000L, 0L)
        assertFalse(r.wajibCbcr)
    }

    // ---------- Kombinasi ----------

    @Test
    fun `grup besar memenuhi semua kewajiban`() {
        val r = ThresholdCalculator.hitung(15_000_000_000_000L, 30_000_000_000L)
        assertTrue(r.wajibMasterLocal)
        assertTrue(r.wajibCbcr)
    }

    @Test
    fun `nilai negatif diperlakukan sebagai nol`() {
        val r = ThresholdCalculator.hitung(-10_000_000_000L, -5_000_000_000L)
        assertFalse(r.wajibMasterLocal)
        assertFalse(r.wajibCbcr)
        assertEquals(0L, r.omzetGrup)
        assertEquals(0L, r.transaksiAfiliasi)
    }

    // ---------- Pesan alasan ----------

    @Test
    fun `alasan menyebutkan angka threshold`() {
        val r = ThresholdCalculator.hitung(60_000_000_000L, 25_000_000_000L)
        assertTrue(r.alasanMasterLocal.contains("Rp50 M"))
        assertTrue(r.alasanMasterLocal.contains("Rp20 M"))
    }
}