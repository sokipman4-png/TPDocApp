package com.tpdoc.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tpdoc.app.data.ContentData
import com.tpdoc.app.ui.components.BulletList
import com.tpdoc.app.ui.components.ChecklistGroup
import com.tpdoc.app.ui.components.InfoCard
import com.tpdoc.app.ui.components.ScreenScaffold
import com.tpdoc.app.ui.components.SectionHeader
import com.tpdoc.app.ui.components.WarningBanner
import com.tpdoc.app.ui.viewmodel.ChecklistViewModel

@Composable
fun DokumenScreen(onBack: () -> Unit) {
    val vm: ChecklistViewModel = viewModel()
    val itemsDokumen by vm.dokumen.collectAsState(initial = emptyList())
    val itemsPersiapan by vm.persiapan.collectAsState(initial = emptyList())

    ScreenScaffold(title = "Dokumen TP", onBack = onBack) {
        SectionHeader("Kewajiban Dokumentasi TP Doc")
        ContentData.dokumenTp.forEach { d ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            "${d.kode} — ${d.nama}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Text(d.tujuan, style = MaterialTheme.typography.bodyMedium)
                    Text("Isi utama:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                    BulletList(d.isiUtama)
                    InfoCard(
                        title = "Kapan wajib",
                        body = d.kapanWajib,
                        icon = Icons.Default.ReceiptLong,
                    )
                }
            }
        }

        SectionHeader("Lampiran SPT Tahunan Badan")
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                ContentData.lampiranSpt.forEach { (kode, keterangan) ->
                    Text(
                        "•  Form ${kode}: $keterangan",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }

        ChecklistGroup(
            title = "Checklist Dokumen (persisten di perangkat)",
            items = itemsDokumen,
            onToggle = vm::toggle,
        )

        ChecklistGroup(
            title = "Checklist Persiapan Kepatuhan",
            items = itemsPersiapan,
            onToggle = vm::toggle,
        )

        WarningBanner("Simpan dokumentasi sebagai bukti: DJP dapat meminta dokumen saat pemeriksaan. Hitung mundur kewajiban notifikasi CbCR tersedia di menu Notifikasi.")
    }
}