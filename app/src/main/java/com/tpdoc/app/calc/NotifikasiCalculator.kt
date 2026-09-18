package com.tpdoc.app.calc

import java.util.Calendar
import java.util.GregorianCalendar

/**
 * Perhitungan jadwal Notifikasi CbCR.
 *
 * Aturan dokumen: notifikasi CbCR disampaikan elektronik paling lama 12 bulan
 * setelah akhir tahun pajak. Asumsi tahun buku = tahun kalender, sehingga akhir
 * tahun pajak = 31 Desember tahun pajak, jatuh tempo = 31 Desember tahun berikutnya.
 *
 * sengaja memakai java.util.Calendar agar kompatibel minSdk 24 tanpa desugaring.
 */
object NotifikasiCalculator {

    data class Reminder(
        val fiscalYear: Int,
        val endOfTaxYear: Calendar,
        val dueDate: Calendar,
        val daysRemaining: Long,   // negatif = sudah lewat jatuh tempo
        val status: Status,
    )

    enum class Status { JAUH, MENDEKAT, SEGERA, TERLAMBAT }

    const val BATAS_HARI_MENDEKAT: Long = 180    // 6 bulan
    const val BATAS_HARI_SEGERA: Long = 30       // 1 bulan

    fun endOfTaxYear(fiscalYear: Int): Calendar =
        GregorianCalendar(fiscalYear, Calendar.DECEMBER, 31, 0, 0, 0).apply {
            isLenient = true
            set(Calendar.MILLISECOND, 0)
        }

    fun dueDate(fiscalYear: Int): Calendar =
        endOfTaxYear(fiscalYear).apply { add(Calendar.MONTH, 12) }

    fun daysRemaining(fiscalYear: Int, today: Calendar): Long {
        val due = dueDate(fiscalYear)
        val diffMillis = due.timeInMillis - today.timeInMillis
        val days = diffMillis / (24L * 60 * 60 * 1000)
        // pembulatan ke atas agar hari ini == 0 tidak langsung "terlambat"
        return if (diffMillis % (24L * 60 * 60 * 1000) > 0) days + 1 else days
    }

    fun status(fiscalYear: Int, today: Calendar): Status {
        val sisa = daysRemaining(fiscalYear, today)
        return when {
            sisa < 0 -> Status.TERLAMBAT
            sisa <= BATAS_HARI_SEGERA -> Status.SEGERA
            sisa <= BATAS_HARI_MENDEKAT -> Status.MENDEKAT
            else -> Status.JAUH
        }
    }

    fun reminder(fiscalYear: Int, today: Calendar): Reminder =
        Reminder(
            fiscalYear = fiscalYear,
            endOfTaxYear = endOfTaxYear(fiscalYear),
            dueDate = dueDate(fiscalYear),
            daysRemaining = daysRemaining(fiscalYear, today),
            status = status(fiscalYear, today),
        )
}