package com.tpdoc.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tpdoc.app.ui.components.AIAnalisisDialog
import com.tpdoc.app.ui.viewmodel.AIAnalisisViewModel
import com.tpdoc.app.ui.viewmodel.AnalisisViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalisisScreen(
    perusahaanId: Long,
    onBack: () -> Unit,
    nonAiVm: AnalisisViewModel = viewModel(),
    aiVm: AIAnalisisViewModel = viewModel(),
) {
    val nonAiState by nonAiVm.uiState.collectAsState()
    val aiState by aiVm.uiState.collectAsState()

    LaunchedEffect(perusahaanId) {
        nonAiVm.loadPerusahaan(perusahaanId)
        aiVm.loadPerusahaan(perusahaanId)
    }

    LaunchedEffect(nonAiState.estimasiSanksi) {
        if (nonAiState.estimasiSanksi.isNotBlank()) {
            aiVm.setNonAiResult(nonAiState.estimasiSanksi)
        }
    }

    // AI Confirm Dialog
    if (aiState.showConfirmDialog) {
        AIAnalisisDialog(
            components = aiState.components,
            estimate = aiState.costEstimate,
            onComponentToggle = { aiVm.toggleComponent(it) },
            onSelectAll = { aiVm.selectAllComponents() },
            onDeselectAll = { aiVm.deselectAllComponents() },
            onConfirm = { aiVm.runAnalisis() },
            onDismiss = { aiVm.dismissConfirmDialog() },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analisis ${nonAiState.perusahaan?.nama ?: ""}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Non-AI Section
            Text("Analisis Non-AI", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            
            // Kalkulator Threshold
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Kalkulator Threshold", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    
                    OutlinedTextField(
                        value = nonAiState.omzetGrup,
                        onValueChange = { nonAiVm.updateOmzet(it) },
                        label = { Text("Omzet Konsolidasi Grup (Rp)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    OutlinedTextField(
                        value = nonAiState.transaksiAfiliasi,
                        onValueChange = { nonAiVm.updateTransaksi(it) },
                        label = { Text("Total Transaksi Afiliasi (Rp)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )

                    nonAiState.hasilThreshold?.let { hasil ->
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        StatusRow("Master File", hasil.wajibMasterLocal)
                        StatusRow("Local File", hasil.wajibMasterLocal)
                        StatusRow("CbCR", hasil.wajibCbcr)
                    }
                }
            }

            // Checklist Pihak Berelasi
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Checklist Pihak Berelasi", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    nonAiState.checklistBerelasi.forEachIndexed { i, (label, checked) ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = checked, onCheckedChange = { nonAiVm.toggleBerelasi(i) })
                            Text(label, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            // Estimasi Sanksi
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Estimasi Sanksi", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(nonAiState.estimasiSanksi, style = MaterialTheme.typography.bodyMedium)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            // AI Section
            Text("Analisis AI", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            // AI Button
            Button(
                onClick = { aiVm.showConfirmDialog() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !aiState.isLoading,
            ) {
                Text(if (aiState.isLoading) "Menganalisis..." else "Analisis dengan AI")
            }

            // Loading
            if (aiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            // Error
            aiState.error?.let { err ->
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Error: $err", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onErrorContainer)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { aiVm.runAnalisis() }) { Text("Retry") }
                        }
                    }
                }
            }

            // AI Result
            aiState.aiResult?.let { result ->
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Hasil Analisis AI", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(result, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            // Actual Cost
            aiState.actualCost?.let { cost ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Biaya Aktual", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(cost, style = MaterialTheme.typography.bodySmall)
                        aiState.actualCostEstimate?.let { est ->
                            Text("Selisih: \$${String.format("%.6f", est.totalUsd - (aiState.costEstimate.totalUsd))}",
                                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusRow(label: String, wajib: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("$label:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        Text(
            if (wajib) "WAJIB" else "TIDAK WAJIB",
            style = MaterialTheme.typography.bodyMedium,
            color = if (wajib) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Bold,
        )
    }
}