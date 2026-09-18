package com.tpdoc.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tpdoc.app.data.ai.ApiKeyManager
import com.tpdoc.app.data.ai.OpenRouterClient
import com.tpdoc.app.data.ai.OpenRouterModel
import com.tpdoc.app.data.datastore.SettingsRepository
import com.tpdoc.app.data.room.ModelCache
import com.tpdoc.app.data.room.ModelCacheDao
import com.tpdoc.app.data.room.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val apiKeySet: Boolean = false,
    val maskedKey: String = "",
    val testResult: String? = null,
    val testLoading: Boolean = false,
    val selectedModelId: String = SettingsRepository.DEFAULT_MODEL,
    val exchangeRate: Float = SettingsRepository.DEFAULT_EXCHANGE_RATE,
    val exchangeRateText: String = "16000",
    val promptTemplate: String = "",
    val promptError: String? = null,
    val promptPreview: String? = null,
    val models: List<ModelCache> = emptyList(),
    val modelSearchQuery: String = "",
    val modelPage: Int = 0,
    val modelTotalPages: Int = 0,
    val loadingModels: Boolean = false,
    val modelRefreshing: Boolean = false,
    val disclaimerAccepted: Boolean = false,
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settings = SettingsRepository(application)
    private val modelCacheDao: ModelCacheDao = AppDatabase.get(application).modelCacheDao()

    init {
        ApiKeyManager.init(application)
    }

    private val _uiState = MutableStateFlow(SettingsUiState(
        apiKeySet = ApiKeyManager.hasApiKey(),
        maskedKey = ApiKeyManager.getMaskedKey(),
    ))
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settings.selectedModel.collect { model ->
                _uiState.value = _uiState.value.copy(selectedModelId = model)
            }
        }
        viewModelScope.launch {
            settings.exchangeRate.collect { rate ->
                _uiState.value = _uiState.value.copy(
                    exchangeRate = rate,
                    exchangeRateText = rate.toInt().toString(),
                )
            }
        }
        viewModelScope.launch {
            settings.promptTemplate.collect { template ->
                _uiState.value = _uiState.value.copy(promptTemplate = template)
            }
        }
        viewModelScope.launch {
            modelCacheDao.observeAll().collect { models ->
                val totalPages = if (models.isEmpty()) 0 else (models.size + 9) / 10  // 10 per page
                _uiState.value = _uiState.value.copy(
                    models = models,
                    modelTotalPages = totalPages,
                )
            }
        }
        loadCachedModelsIfAvailable()
    }

    // API Key
    fun saveApiKey(key: String) {
        ApiKeyManager.setApiKey(key)
        _uiState.value = _uiState.value.copy(
            apiKeySet = true,
            maskedKey = ApiKeyManager.getMaskedKey(),
            testResult = null,
        )
    }

    fun removeApiKey() {
        ApiKeyManager.removeApiKey()
        _uiState.value = _uiState.value.copy(
            apiKeySet = false,
            maskedKey = "",
            testResult = null,
        )
    }

    fun testApiConnection() {
        val key = ApiKeyManager.getApiKey() ?: run {
            _uiState.value = _uiState.value.copy(testResult = "Gagal: API key kosong")
            return
        }
        _uiState.value = _uiState.value.copy(testLoading = true, testResult = null)
        viewModelScope.launch {
            try {
                val client = OpenRouterClient(key)
                val result = client.testConnection()
                result.fold(
                    onSuccess = { latency ->
                        _uiState.value = _uiState.value.copy(
                            testLoading = false,
                            testResult = "Sukses! Latency: ${latency}ms",
                        )
                    },
                    onFailure = { e ->
                        _uiState.value = _uiState.value.copy(
                            testLoading = false,
                            testResult = "Gagal: ${e.message}",
                        )
                    },
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    testLoading = false,
                    testResult = "Gagal: ${e.message}",
                )
            }
        }
    }

    // Model management
    fun refreshModels() {
        val key = ApiKeyManager.getApiKey() ?: return
        _uiState.value = _uiState.value.copy(modelRefreshing = true)
        viewModelScope.launch {
            try {
                val client = OpenRouterClient(key)
                val models = client.fetchModels()
                val cacheEntries = models.map { m ->
                    ModelCache(
                        id = m.id,
                        name = m.name,
                        promptPrice = m.pricing.prompt,
                        completionPrice = m.pricing.completion,
                        contextLength = m.context_length,
                    )
                }
                modelCacheDao.clearAll()
                modelCacheDao.insertAll(cacheEntries)
                _uiState.value = _uiState.value.copy(modelRefreshing = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    modelRefreshing = false,
                    testResult = "Gagal fetch model: ${e.message}",
                )
            }
        }
    }

    private fun loadCachedModelsIfAvailable() {
        viewModelScope.launch {
            val count = modelCacheDao.count()
            if (count == 0) {
                _uiState.value = _uiState.value.copy(loadingModels = false)
            } else {
                val lastCached = modelCacheDao.lastCachedAt()
                val now = System.currentTimeMillis()
                val cacheAge = (now - (lastCached ?: 0)) / 3600_000  // hours
                if (cacheAge >= 24) {
                    // Cache expired, but still show cached data
                    _uiState.value = _uiState.value.copy(loadingModels = false)
                }
            }
        }
    }

    fun searchModels(query: String) {
        _uiState.value = _uiState.value.copy(modelSearchQuery = query, modelPage = 0)
    }

    fun setModelPage(page: Int) {
        _uiState.value = _uiState.value.copy(modelPage = page)
    }

    fun selectModel(modelId: String) {
        _uiState.value = _uiState.value.copy(selectedModelId = modelId)
        viewModelScope.launch { settings.setSelectedModel(modelId) }
    }

    // Exchange rate
    fun updateExchangeRate(rate: String) {
        val f = rate.toFloatOrNull() ?: return
        _uiState.value = _uiState.value.copy(exchangeRateText = rate, exchangeRate = f)
        viewModelScope.launch { settings.setExchangeRate(f) }
    }

    // Prompt
    fun updatePromptTemplate(template: String) {
        _uiState.value = _uiState.value.copy(
            promptTemplate = template,
            promptError = validatePrompt(template),
        )
    }

    fun savePrompt() {
        val template = _uiState.value.promptTemplate
        val err = validatePrompt(template)
        if (err != null) {
            _uiState.value = _uiState.value.copy(promptError = err)
            return
        }
        viewModelScope.launch {
            settings.setPromptTemplate(template)
            _uiState.value = _uiState.value.copy(promptError = null)
        }
    }

    fun resetPrompt() {
        viewModelScope.launch {
            settings.resetPromptToDefault()
        }
    }

    fun previewPrompt() {
        val template = _uiState.value.promptTemplate
        val preview = template
            .replace("{data_perusahaan}", """{"nama": "PT Contoh", "npwp": "01.234.567.8-901.000"}""")
            .replace("{komponen_analisis}", "Profil perusahaan, Kewajaran transaksi, Rekomendasi")
            .replace("{nama_perusahaan}", "PT Contoh")
            .replace("{npwp}", "01.234.567.8-901.000")
            .replace("{omzet}", "100000000000")
            .replace("{transaksi_afiliasi}", "50000000000")
        _uiState.value = _uiState.value.copy(promptPreview = preview)
    }

    private fun validatePrompt(template: String): String? {
        if (!template.contains("{data_perusahaan}")) return "Prompt harus mengandung placeholder {data_perusahaan}"
        if (!template.contains("{komponen_analisis}")) return "Prompt harus mengandung placeholder {komponen_analisis}"
        return null
    }
}