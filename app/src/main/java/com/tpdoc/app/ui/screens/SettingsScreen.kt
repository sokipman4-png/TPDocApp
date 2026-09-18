package com.tpdoc.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tpdoc.app.ui.viewmodel.ExportViewModel
import com.tpdoc.app.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    vm: SettingsViewModel = viewModel(),
    exportVm: ExportViewModel = viewModel(),
) {
    val state by vm.uiState.collectAsState()
    val exportState by exportVm.uiState.collectAsState()
    var showPromptPreview by remember { mutableStateOf(false) }
    var keyInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan AI") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Section: API Key
            item {
                SectionTitle("API Key OpenRouter")
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (state.apiKeySet) {
                            Text("Tersimpan: ${state.maskedKey}", style = MaterialTheme.typography.bodyMedium)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { vm.testApiConnection() },
                                    enabled = !state.testLoading,
                                ) {
                                    Text(if (state.testLoading) "Menguji..." else "Test")
                                }
                                OutlinedButton(onClick = { vm.removeApiKey() }) {
                                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        } else {
                            OutlinedTextField(
                                value = keyInput,
                                onValueChange = { keyInput = it },
                                label = { Text("Masukkan API Key") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation(),
                            )
                            Button(
                                onClick = {
                                    if (keyInput.isNotBlank() && !keyInput.startsWith("sk-or-v1-")) {
                                        // Validation warning - but still allow save
                                    }
                                    vm.saveApiKey(keyInput.trim())
                                    keyInput = ""
                                },
                            ) {
                                Text("Simpan")
                            }
                        }
                        state.testResult?.let {
                            Text(it, style = MaterialTheme.typography.bodySmall, color = if (it.startsWith("S")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            // Section: Model
            item {
                SectionTitle("Model AI")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { vm.refreshModels() }, enabled = state.apiKeySet && !state.modelRefreshing) {
                        Text(if (state.modelRefreshing) "Memperbarui..." else "Refresh Daftar Model")
                    }
                }
                Spacer(Modifier.height(4.dp))
            }

            // Model search
            if (state.models.isNotEmpty()) {
                item {
                    OutlinedTextField(
                        value = state.modelSearchQuery,
                        onValueChange = { vm.searchModels(it) },
                        label = { Text("Cari model...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                }

                // Model list with pagination
                val filteredModels = if (state.modelSearchQuery.isNotBlank()) {
                    val q = state.modelSearchQuery.lowercase()
                    state.models.filter { it.id.lowercase().contains(q) || it.name.lowercase().contains(q) }
                } else {
                    state.models
                }
                val pageSize = 10
                val pageStart = state.modelPage * pageSize
                val pageModels = filteredModels.drop(pageStart).take(pageSize)
                val filteredTotalPages = if (filteredModels.isEmpty()) 0 else (filteredModels.size + pageSize - 1) / pageSize

                item {
                    Text("Halaman ${state.modelPage + 1}/$filteredTotalPages (${filteredModels.size} model)",
                        style = MaterialTheme.typography.labelMedium)
                }

                items(pageModels) { model ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (model.id == state.selectedModelId)
                                MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surface,
                        ),
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(model.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                            Text("ID: ${model.id}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Input: \$${formatPrice(model.promptPrice)}/1jt token | Output: \$${formatPrice(model.completionPrice)}/1jt token | Context: ${model.contextLength}",
                                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (model.id != state.selectedModelId) {
                                Spacer(Modifier.height(4.dp))
                                Button(
                                    onClick = { vm.selectModel(model.id) },
                                    modifier = Modifier.height(32.dp),
                                    contentPadding = ButtonDefaults.TextButtonContentPadding,
                                ) {
                                    Text("Pilih", style = MaterialTheme.typography.labelSmall)
                                }
                            } else {
                                Text("Terpilih", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Pagination buttons
                if (filteredTotalPages > 1) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            OutlinedButton(
                                onClick = { vm.setModelPage(maxOf(0, state.modelPage - 1)) },
                                enabled = state.modelPage > 0,
                            ) { Text("Prev") }
                            Text("Hal ${state.modelPage + 1}/$filteredTotalPages")
                            OutlinedButton(
                                onClick = { vm.setModelPage(minOf(filteredTotalPages - 1, state.modelPage + 1)) },
                                enabled = state.modelPage < filteredTotalPages - 1,
                            ) { Text("Next") }
                        }
                    }
                }
            }

            // Section: Exchange Rate
            item {
                SectionTitle("Kurs USD → IDR")
                OutlinedTextField(
                    value = state.exchangeRateText,
                    onValueChange = { vm.updateExchangeRate(it) },
                    label = { Text("Rp per 1 USD") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
            }

            // Section: Prompt Template
            item {
                SectionTitle("Template Prompt AI")
                OutlinedTextField(
                    value = state.promptTemplate,
                    onValueChange = { vm.updatePromptTemplate(it) },
                    label = { Text("Edit Prompt") },
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    maxLines = 10,
                )
                state.promptError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { vm.savePrompt() }) { Text("Simpan Prompt") }
                    OutlinedButton(onClick = { vm.resetPrompt() }) { Text("Reset ke Default") }
                    OutlinedButton(onClick = {
                        vm.previewPrompt()
                        showPromptPreview = true
                    }) { Text("Preview Prompt") }
                }
            }

            // Section: Backup & Restore
            item {
                SectionTitle("Backup & Restore")
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "Data 100% lokal. Backup rutin untuk menghindari kehilangan data jika HP hilang/rusak.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { exportVm.backupJson() }) { Text("Backup (JSON)") }
                            OutlinedButton(onClick = { exportVm.pickRestoreFile() }) { Text("Restore (JSON)") }
                        }
                    }
                }
            }

            // Section: Disclaimer
            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                Text(
                    "Disclaimer: Data perusahaan akan dikirim ke OpenRouter (pihak ketiga) saat analisis AI dijalankan.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }

    // Preview dialog
    if (showPromptPreview) {
        AlertDialog(
            onDismissRequest = { showPromptPreview = false },
            title = { Text("Preview Prompt") },
            text = { Text(state.promptPreview ?: "Tidak ada preview") },
            confirmButton = {
                TextButton(onClick = { showPromptPreview = false }) { Text("Tutup") }
            },
        )
    }

    // Status export/backup
    exportState.statusMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = { exportVm.clearStatus() },
            title = { Text("Sukses") },
            text = { Text(msg) },
            confirmButton = { TextButton(onClick = { exportVm.clearStatus() }) { Text("OK") } },
        )
    }
    exportState.error?.let { msg ->
        AlertDialog(
            onDismissRequest = { exportVm.clearStatus() },
            title = { Text("Error") },
            text = { Text(msg) },
            confirmButton = { TextButton(onClick = { exportVm.clearStatus() }) { Text("OK") } },
        )
    }

    // Restore confirmation dialog
    if (exportState.showRestoreConfirm) {
        AlertDialog(
            onDismissRequest = { exportVm.cancelRestore() },
            title = { Text("Konfirmasi Restore") },
            text = { Text("Data existing akan ditimpa!\n\n${exportState.pendingRestore.size} perusahaan akan terimport dari backup. Data saat ini akan dihapus. Lanjut?") },
            confirmButton = { TextButton(onClick = { exportVm.confirmRestore() }) { Text("Lanjut", color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { exportVm.cancelRestore() }) { Text("Batal") } },
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
    )
}

private fun formatPrice(priceStr: String): String {
    return try {
        val price = priceStr.toDouble()
        "%.4f".format(price * 1_000_000)
    } catch (e: Exception) {
        priceStr
    }
}