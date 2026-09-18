package com.tpdoc.app.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tpdoc.app.ui.components.HelpButton
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.tpdoc.app.ui.viewmodel.DashboardViewModel
import com.tpdoc.app.ui.viewmodel.ExportViewModel
import com.tpdoc.app.ui.viewmodel.StatusKepatuhan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onBack: (() -> Unit)? = null,
    onSettings: (() -> Unit)? = null,
    vm: DashboardViewModel = viewModel(),
    exportVm: ExportViewModel = viewModel(),
) {
    val state by vm.uiState.collectAsState()
    val exportState by exportVm.uiState.collectAsState()
    val activityContext = androidx.compose.ui.platform.LocalContext.current
    val exportPdfLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/pdf"),
    ) { uri -> uri?.let { exportVm.exportPdfTo(it) } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard Grup") },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                        }
                    }
                },
                actions = {
                    if (onSettings != null) {
                        IconButton(onClick = onSettings) {
                            Icon(Icons.Default.Settings, contentDescription = "Pengaturan")
                        }
                    }
                    HelpButton(
                        "Dashboard: samenvatting van de gehele concern met status (hijau/kuning/merah), " +
                            "omzet, hiërarchie-grafiek en export/share. Productie: klik een bedrijf aan " +
                            "of open het detailscherm via de bedrijvenlijst.",
                        contentDescription = "Panduan dashboard",
                    )
                    IconButton(onClick = { exportPdfLauncher.launch("tpdoc_laporan_grup.pdf") }) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "Export PDF Grup")
                    }
                    IconButton(onClick = { exportVm.shareCsv(activityContext) }) {
                        Icon(Icons.Default.Share, contentDescription = "Share Laporan")
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
        // Header
        item {
            Column {
                Text(
                    "Dashboard Grup",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    "${state.totalPerusahaan} perusahaan terdaftar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // Status summary cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StatusCard(
                    label = "Patuh",
                    count = state.hijau,
                    color = androidx.compose.ui.graphics.Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f),
                )
                StatusCard(
                    label = "Perlu Tindakan",
                    count = state.kuning,
                    color = androidx.compose.ui.graphics.Color(0xFFFFC107),
                    modifier = Modifier.weight(1f),
                )
                StatusCard(
                    label = "Belum Patuh",
                    count = state.merah,
                    color = androidx.compose.ui.graphics.Color(0xFFF44336),
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // Legend
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Keterangan Warna", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                    LegendRow("Hijau", "Patuh — semua dokumen TP sudah disiapkan", androidx.compose.ui.graphics.Color(0xFF4CAF50))
                    LegendRow("Kuning", "Perlu Tindakan — threshold terpenuhi tapi belum lengkap", androidx.compose.ui.graphics.Color(0xFFFFC107))
                    LegendRow("Merah", "Belum Patuh — threshold terpenuhi, tidak ada dokumen", androidx.compose.ui.graphics.Color(0xFFF44336))
                }
            }
        }

        // Bar Chart
        if (state.totalPerusahaan > 0) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Status Kepatuhan", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        AndroidView(
                            factory = { context ->
                                HorizontalBarChart(context).apply {
                                    description.isEnabled = false
                                    setFitBars(true)
                                    setScaleEnabled(false)
                                    legend.isEnabled = false
                                    axisLeft.axisMinimum = 0f
                                    axisLeft.granularity = 1f
                                    axisRight.isEnabled = false
                                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                                    xAxis.granularity = 1f
                                    xAxis.setDrawGridLines(false)
                                }
                            },
                            update = { chart ->
                                val entries = listOf(
                                    BarEntry(0f, state.hijau.toFloat()),
                                    BarEntry(1f, state.kuning.toFloat()),
                                    BarEntry(2f, state.merah.toFloat()),
                                )
                                val dataSet = BarDataSet(entries, "").apply {
                                    colors = listOf(
                                        Color.rgb(76, 175, 80),
                                        Color.rgb(255, 193, 7),
                                        Color.rgb(244, 67, 54),
                                    )
                                    valueTextSize = 12f
                                }
                                chart.data = BarData(dataSet)
                                chart.xAxis.valueFormatter = IndexAxisValueFormatter(listOf("Patuh", "Perlu", "Belum"))
                                chart.invalidate()
                            },
                            modifier = Modifier.fillMaxWidth().height(160.dp),
                        )
                    }
                }
            }
        }

        // Table ringkasan
        item {
            Text("Tabel Ringkasan", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }
        items(state.summaries) { summary ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Status indicator dot
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(
                                when (summary.statusKepatuhan) {
                                    StatusKepatuhan.HIJAU -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
                                    StatusKepatuhan.KUNING -> androidx.compose.ui.graphics.Color(0xFFFFC107)
                                    StatusKepatuhan.MERAH -> androidx.compose.ui.graphics.Color(0xFFF44336)
                                }
                            ),
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            summary.perusahaan.nama,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                        )
                        Text(
                            "${summary.perusahaan.status.replaceFirstChar { it.uppercase() }} | NPWP: ${summary.perusahaan.npwp}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text(
                        summary.statusLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = when (summary.statusKepatuhan) {
                            StatusKepatuhan.HIJAU -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
                            StatusKepatuhan.KUNING -> androidx.compose.ui.graphics.Color(0xFFFFC107)
                            StatusKepatuhan.MERAH -> androidx.compose.ui.graphics.Color(0xFFF44336)
                        },
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        // Hierarki
        if (state.hierarchyData.isNotEmpty()) {
            item {
                Text("Bagan Hierarki Grup", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            }
            state.hierarchyData.forEach { (induk, anakList, cucuList) ->
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            HierarchyNodeCard(induk.nama, induk.status, isRoot = true)
                            anakList.forEach { anak ->
                                Spacer(Modifier.height(4.dp))
                                HierarchyNodeCard(anak.nama, anak.status, indent = 1)
                                cucuList.filter { it.parentId == anak.id }.forEach { cucu ->
                                    Spacer(Modifier.height(4.dp))
                                    HierarchyNodeCard(cucu.nama, cucu.status, indent = 2)
                                }
                            }
                        }
                    }
                }
            }
        }
        }

        // Status messages
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
    }
}

@Composable
private fun StatusCard(label: String, count: Int, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                count.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = color,
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = color.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun LegendRow(colorName: String, desc: String, color: androidx.compose.ui.graphics.Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            "$colorName: $desc",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun HierarchyNodeCard(nama: String, status: String, isRoot: Boolean = false, indent: Int = 0) {
    val bg = if (isRoot) MaterialTheme.colorScheme.primaryContainer
    else if (indent == 1) MaterialTheme.colorScheme.secondaryContainer
    else MaterialTheme.colorScheme.surfaceVariant

    Card(
        modifier = Modifier.fillMaxWidth().padding(start = (indent * 16).dp),
        colors = CardDefaults.cardColors(containerColor = bg),
    ) {
        Text(
            "$nama (${status.replaceFirstChar { it.uppercase() }})",
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isRoot) FontWeight.Bold else FontWeight.Normal,
        )
    }
}