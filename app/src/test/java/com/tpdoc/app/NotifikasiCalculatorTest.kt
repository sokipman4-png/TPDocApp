package com.tpdoc.app

import com.tpdoc.app.calc.NotifikasiCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

/** Perhitungan notifikasi CbCR: jatuh tempo = 12 bulan setelah akhir tahun pajak (31 Des). */
class NotifikasiCalculatorTest {

    private fun cal(year: Int, month: Int, day: Int): Calendar =
        Calendar.getInstance().apply {
            clear()
            set(year, month, day, 0, 0, 0)
        }

    @Test
    fun `akhir tahun pajak adalah 31 Desember`() {
        val end = NotifikasiCalculator.endOfTaxYear(2024)
        assertEquals(2024, end.get(Calendar.YEAR))
        assertEquals(Calendar.DECEMBER, end.get(Calendar.MONTH))
        assertEquals(31, end.get(Calendar.DAY_OF_MONTH))
    }

    @Test
    fun `jatuh tempo adalah 12 bulan setelah akhir tahun pajak`() {
        val due = NotifikasiCalculator.dueDate(2024)
        assertEquals(2025, due.get(Calendar.YEAR))
        assertEquals(Calendar.DECEMBER, due.get(Calendar.MONTH))
        assertEquals(31, due.get(Calendar.DAY_OF_MONTH))
    }

    @Test
    fun `tahun pajak 2023 jatuh tempo 31 Des 2024`() {
        val due = NotifikasiCalculator.dueDate(2023)
        assertEquals(2024, due.get(Calendar.YEAR))
        assertEquals(31, due.get(Calendar.DAY_OF_MONTH))
    }

    @Test
    fun `enam bulan sebelum jatuh tempo - status mendekat`() {
        // due 31 Des 2025; 1 Agu 2025 => 152 hari sisa (<=180, >30) => MENDEKAT
        val today = cal(2025, Calendar.AUGUST, 1)
        val status = NotifikasiCalculator.status(2024, today)
        assertEquals(NotifikasiCalculator.Status.MENDEKAT, status)
    }

    @Test
    fun `dua minggu sebelum jatuh tempo - status segera`() {
        val today = cal(2025, Calendar.DECEMBER, 17)
        assertEquals(NotifikasiCalculator.Status.SEGERA, NotifikasiCalculator.status(2024, today))
    }

    @Test
    fun `hari jatuh tempo - status segera bukan terlambat`() {
        val today = cal(2025, Calendar.DECEMBER, 31)
        val sisa = NotifikasiCalculator.daysRemaining(2024, today)
        assertTrue("sisa hari pada H-0 harus 0, aktual $sisa", sisa == 0L)
        assertEquals(NotifikasiCalculator.Status.SEGERA, NotifikasiCalculator.status(2024, today))
    }

    @Test
    fun `setelah jatuh tempo - status terlambat dengan sisa negatif`() {
        val today = cal(2026, Calendar.JANUARY, 15)
        val r = NotifikasiCalculator.reminder(2024, today)
        assertEquals(NotifikasiCalculator.Status.TERLAMBAT, r.status)
        assertTrue("harus negatif: ${r.daysRemaining}", r.daysRemaining < 0)
    }

    @Test
    fun `jauh sebelum jatuh tempo - status jauh`() {
        val today = cal(2025, Calendar.JANUARY, 1)
        assertEquals(NotifikasiCalculator.Status.JAUH, NotifikasiCalculator.status(2024, today))
    }

    @Test
    fun `tahun kabisat tidak mengubah aturan 12 bulan`() {
        // 2028 kabisat; akhir tahun pajak 2028 = 31 Des 2028, due = 31 Des 2029
        val due = NotifikasiCalculator.dueDate(2028)
        assertEquals(2029, due.get(Calendar.YEAR))
        assertEquals(31, due.get(Calendar.DAY_OF_MONTH))
    }
}