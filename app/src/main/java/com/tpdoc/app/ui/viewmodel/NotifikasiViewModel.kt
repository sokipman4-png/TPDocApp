package com.tpdoc.app.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tpdoc.app.calc.NotifikasiCalculator
import com.tpdoc.app.data.datastore.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

class NotifikasiViewModel(application: Application) : AndroidViewModel(application) {

    private val settings = SettingsRepository(application)

    var fiscalYear by mutableStateOf(SettingsRepository.DEFAULT_FISCAL_YEAR)
        private set

    var inputTahun by mutableStateOf(SettingsRepository.DEFAULT_FISCAL_YEAR.toString())
        private set

    init {
        viewModelScope.launch {
            fiscalYear = settings.fiscalYear.first()
            inputTahun = fiscalYear.toString()
        }
    }

    fun updateInput(v: String) {
        inputTahun = v.filter { it.isDigit() }.take(4)
    }

    fun simpan() {
        val tahun = inputTahun.toIntOrNull()
        if (tahun != null && tahun in 2000..2100) {
            viewModelScope.launch {
                settings.setFiscalYear(tahun)
                fiscalYear = settings.fiscalYear.first()
            }
        }
    }

    fun reminder(today: Calendar = Calendar.getInstance()): NotifikasiCalculator.Reminder =
        NotifikasiCalculator.reminder(fiscalYear, today)
}