package com.tpdoc.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tpdoc.app.data.ai.ApiKeyManager
import com.tpdoc.app.data.ai.ChatMessage
import com.tpdoc.app.data.ai.OpenRouterClient
import com.tpdoc.app.data.datastore.SettingsRepository
import com.tpdoc.app.data.room.AppDatabase
import com.tpdoc.app.data.room.ModelCacheDao
import com.tpdoc.app.data.room.Perusahaan
import com.tpdoc.app.data.room.PerusahaanRepository
import com.tpdoc.app.ui.components.AnalisisComponent
import com.tpdoc.app.ui.components.CostEstimate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class AIAnalisisUiState(
    val perusahaan: Perusahaan? = null,
    val dataJson: String = "",
    val components: List<AnalisisComponent> = listOf(
        AnalisisComponent("profil", "Profil perusahaan dan status kewajiban TP Doc"),
        AnalisisComponent("kewajaran", "Kewajaran transaksi afiliasi"),
        AnalisisComponent("rekomendasi_dokumen", "Rekomendasi dokumen yang wajib disiapkan"),
        AnalisisComponent("risiko", "Estimasi risiko ketidakpatuhan"),
        AnalisisComponent("tindakan", "Rekomendasi tindakan perbaikan"),
        AnalisisComponent("sanksi", "Estimasi sanksi jika tidak patuh"),
    ),
    val showConfirmDialog: Boolean = false,
    val costEstimate: CostEstimate = CostEstimate(),
    val isLoading: Boolean = false,
    val aiResult: String? = null,
    val aiResultJson: Map<String, String> = emptyMap(),
    val actualCost: String? = null,
    val actualCostEstimate: CostEstimate? = null,
    val error: String? = null,
    val nonAiResult: String = "",
)

class AIAnalisisViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = PerusahaanRepository(application)
    private val settings = SettingsRepository(application)
    private val modelCacheDao: ModelCacheDao = AppDatabase.get(application).modelCacheDao()

    init {
        ApiKeyManager.init(application)
    }

    private val _uiState = MutableStateFlow(AIAnalisisUiState())
    val uiState: StateFlow<AIAnalisisUiState> = _uiState.asStateFlow()

    fun loadPerusahaan(id: Long) {
        viewModelScope.launch {
            val p = repo.getById(id) ?: return@launch
            val json = buildJsonData(p)
            _uiState.value = _uiState.value.copy(perusahaan = p, dataJson = json)
        }
    }

    fun setNonAiResult(result: String) {
        _uiState.value = _uiState.value.copy(nonAiResult = result)
    }

    fun showConfirmDialog() {
        viewModelScope.launch {
            val selectedModelId = settings.selectedModel.first()
            val models = modelCacheDao.getAll()
            val model = models.find { it.id == selectedModelId }
            val promptPrice = model?.promptPrice?.toDoubleOrNull() ?: 0.0
            val completionPrice = model?.completionPrice?.toDoubleOrNull() ?: 0.0
            val rate = settings.exchangeRate.first()

            val s = _uiState.value
            val jsonLen = s.dataJson.length
            val promptLen = 1000  // approximate template length
            val inputTokens = ((jsonLen + promptLen) / 4).coerceAtLeast(50)
            val selectedCount = s.components.count { it.enabled }
            val outputTokens = selectedCount * 250

            val inputUsd = inputTokens / 1_000_000.0 * promptPrice
            val outputUsd = outputTokens / 1_000_000.0 * completionPrice
            val totalUsd = inputUsd + outputUsd

            _uiState.value = _uiState.value.copy(
                showConfirmDialog = true,
                costEstimate = CostEstimate(
                    inputTokens = inputTokens,
                    outputTokens = outputTokens,
                    promptPrice = promptPrice,
                    completionPrice = completionPrice,
                    totalUsd = totalUsd,
                    exchangeRate = rate,
                ),
            )
        }
    }

    fun dismissConfirmDialog() {
        _uiState.value = _uiState.value.copy(showConfirmDialog = false)
    }

    fun toggleComponent(index: Int) {
        val list = _uiState.value.components.toMutableList()
        list[index] = list[index].copy(enabled = !list[index].enabled)
        _uiState.value = _uiState.value.copy(components = list)
    }

    fun selectAllComponents() {
        val list = _uiState.value.components.map { it.copy(enabled = true) }
        _uiState.value = _uiState.value.copy(components = list)
    }

    fun deselectAllComponents() {
        val list = _uiState.value.components.map { it.copy(enabled = false) }
        _uiState.value = _uiState.value.copy(components = list)
    }

    fun runAnalisis() {
        _uiState.value = _uiState.value.copy(showConfirmDialog = false, isLoading = true, error = null, aiResult = null)

        viewModelScope.launch {
            val apiKey = ApiKeyManager.getApiKey()
            if (apiKey == null) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "API key tidak tersedia")
                return@launch
            }

            val selectedModelId = settings.selectedModel.first()
            val promptTemplate = settings.promptTemplate.first()
            val s = _uiState.value
            val selectedComponents = s.components.filter { it.enabled }.joinToString(", ") { it.label }
            val prompt = promptTemplate
                .replace("{data_perusahaan}", s.dataJson)
                .replace("{komponen_analisis}", selectedComponents)

            val client = OpenRouterClient(apiKey)

            // Try up to 2 times
            var lastError: String? = null
            for (attempt in 1..2) {
                val result = client.chat(
                    model = selectedModelId,
                    messages = listOf(ChatMessage(role = "user", content = prompt)),
                )
                result.fold(
                    onSuccess = { response ->
                        val aiText = response.choices.firstOrNull()?.message?.content ?: "Tidak ada respon"
                        val usage = response.usage

                        // Calculate actual cost
                        val models = modelCacheDao.getAll()
                        val model = models.find { it.id == selectedModelId }
                        val pPrice = model?.promptPrice?.toDoubleOrNull() ?: 0.0
                        val cPrice = model?.completionPrice?.toDoubleOrNull() ?: 0.0
                        val rate = settings.exchangeRate.first()

                        val actualInput = usage?.prompt_tokens ?: 0
                        val actualOutput = usage?.completion_tokens ?: 0
                        val inputUsd = actualInput / 1_000_000.0 * pPrice
                        val outputUsd = actualOutput / 1_000_000.0 * cPrice
                        val totalUsd = inputUsd + outputUsd

                        val actualCost = CostEstimate(
                            inputTokens = actualInput,
                            outputTokens = actualOutput,
                            promptPrice = pPrice,
                            completionPrice = cPrice,
                            totalUsd = totalUsd,
                            exchangeRate = rate,
                        )

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            aiResult = aiText,
                            actualCost = "Input: $actualInput token, Output: $actualOutput token\nBiaya: \$${String.format("%.6f", totalUsd)} (Rp ${com.tpdoc.app.ui.components.formatRupiah(actualCost.totalIdr)})",
                            actualCostEstimate = actualCost,
                        )
                        return@launch
                    },
                    onFailure = { e ->
                        lastError = e.message
                        if (e is com.tpdoc.app.data.ai.RateLimitException && attempt == 1) {
                            // Retry once more after a short delay
                            kotlinx.coroutines.delay(2000)
                        }
                    },
                )
            }

            _uiState.value = _uiState.value.copy(isLoading = false, error = lastError ?: "Gagal analisis")
        }
    }

    private fun buildJsonData(p: Perusahaan): String {
        return """{
  "nama": "${p.nama}",
  "npwp": "${p.npwp}",
  "alamat": "${p.alamat}",
  "negara": "${p.negara}",
  "status": "${p.status}",
  "tahun_pajak": ${p.tahunPajak}
}"""
    }
}