package com.tpdoc.app.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "tpdoc_settings")

/** Penyimpanan preferensi lokal (offline): tahun pajak untuk pengingat CbCR. */
class SettingsRepository(private val context: Context) {

    private val keyFiscalYear = intPreferencesKey("fiscal_year")

    val fiscalYear: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[keyFiscalYear] ?: DEFAULT_FISCAL_YEAR
    }

    suspend fun setFiscalYear(year: Int) {
        context.dataStore.edit { it[keyFiscalYear] = year }
    }

    companion object {
        const val DEFAULT_FISCAL_YEAR = 2024
    }
}