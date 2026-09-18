package com.tpdoc.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.tpdoc.app.ui.viewmodel.AnalisisViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalisisScreen(
    perusahaanId: Long,
    onBack: () -> Unit,
    onAnalisisAI: () -> Unit = {},
    vm: AnalisisViewModel = viewModel(),
) {
    val state by vm.uiState.collectAsState()

    LaunchedEffect(perusahaanId) {
        vm.loadPerusahaan(perusahaanId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analisis ${state.perusahaan?.nama ?: ""}") },
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
            // Kalkulator Threshold
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Kalkulator Threshold", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    
                    OutlinedTextField(
                        value = state.omzetGrup,
                        onValueChange = { vm.updateOmzet(it) },
                        label = { Text("Omzet Konsolidasi Grup (Rp)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    OutlinedTextField(
                        value = state.transaksiAfiliasi,
                        onValueChange = { vm.updateTransaksi(it) },
                        label = { Text("Total Transaksi Afiliasi (Rp)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )

                    state.hasilThreshold?.let { hasil ->
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
                    state.checklistBerelasi.forEachIndexed { i, (label, checked) ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = checked, onCheckedChange = { vm.toggleBerelasi(i) })
                            Text(label, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            // Jenis Transaksi
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Jenis Transaksi Afiliasi", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    state.jenisTransaksi.forEachIndexed { i, (label, checked) ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = checked, onCheckedChange = { vm.toggleTransaksi(i) })
                            Text(label, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            // Status Kewajiban Dokumen
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Status Kewajiban Dokumen", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    DokumenStatusRow("Master File", state.wajibMasterFile)
                    DokumenStatusRow("Local File", state.wajibLocalFile)
                    DokumenStatusRow("CbCR", state.wajibCbcr)
                }
            }

            // Estimasi Sanksi
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Estimasi Sanksi", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(state.estimasiSanksi, style = MaterialTheme.typography.bodyMedium)
                }
            }

            // Tombol Analisis AI
            Button(
                onClick = onAnalisisAI,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Analisis dengan AI")
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

@Composable
private fun DokumenStatusRow(label: String, wajib: Boolean?) {
    val text = when (wajib) {
        true -> "WAJIB disiapkan"
        false -> "TIDAK wajib"
        null -> "Belum dihitung"
    }
    val color = when (wajib) {
        true -> MaterialTheme.colorScheme.error
        false -> MaterialTheme.colorScheme.primary
        null -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("$label:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        Text(text, style = MaterialTheme.typography.bodyMedium, color = color, fontWeight = FontWeight.Bold)
    }
}