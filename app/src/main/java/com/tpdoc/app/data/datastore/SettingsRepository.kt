package com.tpdoc.app.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "tpdoc_settings")

class SettingsRepository(private val context: Context) {

    private val keyFiscalYear = intPreferencesKey("fiscal_year")
    private val keyExchangeRate = floatPreferencesKey("exchange_rate")
    private val keyPromptTemplate = stringPreferencesKey("prompt_template")
    private val keySelectedModel = stringPreferencesKey("selected_model")

    val fiscalYear: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[keyFiscalYear] ?: DEFAULT_FISCAL_YEAR
    }

    val exchangeRate: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[keyExchangeRate] ?: DEFAULT_EXCHANGE_RATE
    }

    val promptTemplate: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[keyPromptTemplate] ?: DEFAULT_PROMPT_TEMPLATE
    }

    val selectedModel: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[keySelectedModel] ?: DEFAULT_MODEL
    }

    suspend fun setFiscalYear(year: Int) {
        context.dataStore.edit { it[keyFiscalYear] = year }
    }

    suspend fun setExchangeRate(rate: Float) {
        context.dataStore.edit { it[keyExchangeRate] = rate }
    }

    suspend fun setPromptTemplate(template: String) {
        context.dataStore.edit { it[keyPromptTemplate] = template }
    }

    suspend fun setSelectedModel(model: String) {
        context.dataStore.edit { it[keySelectedModel] = model }
    }

    suspend fun resetPromptToDefault() {
        context.dataStore.edit { it[keyPromptTemplate] = DEFAULT_PROMPT_TEMPLATE }
    }

    companion object {
        const val DEFAULT_FISCAL_YEAR = 2024
        const val DEFAULT_EXCHANGE_RATE = 16000f
        const val DEFAULT_MODEL = "deepseek/deepseek-chat"

        val DEFAULT_PROMPT_TEMPLATE =
            "Kamu analis TP Doc Indonesia. Berdasarkan data JSON berikut:\n" +
            "{data_perusahaan}\n" +
            "Jawab komponen analisis berikut: {komponen_analisis}.\n" +
            "Output JSON terstruktur dengan key: wajib_tp (boolean), alasan (string), " +
            "dokumen_wajib (array), risiko (string: rendah/sedang/tinggi), rekomendasi (array)."
    }
}