package com.tpdoc.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.tpdoc.app.calc.ThresholdCalculator

/** State kalkulator threshold; logika murni di [ThresholdCalculator] (diuji terpisah). */
class KalkulatorViewModel : ViewModel() {

    var inputOmzet by mutableStateOf("")
        private set

    var inputTransaksi by mutableStateOf("")
        private set

    fun updateOmzet(v: String) {
        inputOmzet = v.filter { it.isDigit() }
    }

    fun updateTransaksi(v: String) {
        inputTransaksi = v.filter { it.isDigit() }
    }

    val omzet: Long get() = parse(inputOmzet)
    val transaksi: Long get() = parse(inputTransaksi)

    val hasil: ThresholdCalculator.Result
        get() = ThresholdCalculator.hitung(omzet, transaksi)

    private fun parse(s: String): Long = s.toLongOrNull() ?: 0L

    companion object {
        fun formatRupiah(v: Long): String = "Rp " + String.format("%,d", v).replace(",", ".")
    }
}