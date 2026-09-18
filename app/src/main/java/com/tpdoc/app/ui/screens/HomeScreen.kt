package com.tpdoc.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tpdoc.app.data.ContentData
import com.tpdoc.app.navigation.Routes
import com.tpdoc.app.ui.components.InfoCard
import com.tpdoc.app.ui.components.ScreenScaffold
import com.tpdoc.app.ui.components.SectionHeader

private data class FiturLink(val label: String, val deskripsi: String, val icon: ImageVector, val route: String)

private val fitur = listOf(
    FiturLink("Kriteria Wajib TP", "Multinasional, holding, cabang/SKP", Icons.Default.Group, Routes.KRITERIA),
    FiturLink("Kalkulator Threshold", "Master/Local & CbCR", Icons.Default.Calculate, Routes.KALKULATOR),
    FiturLink("Pihak Berelasi", "Checklist hubungan istimewa", Icons.Default.People, Routes.BERELASI),
    FiturLink("Jenis Transaksi", "Jual beli, jasa, pinjaman, royalti, sewa", Icons.Default.SwapHoriz, Routes.TRANSAKSI),
    FiturLink("Dokumen TP", "Master File, Local File, CbCR, SPT", Icons.Default.Description, Routes.DOKUMEN),
    FiturLink("Sanksi", "Koreksi, bunga, denda, P3B", Icons.Default.Gavel, Routes.SANKSI),
    FiturLink("Kesimpulan", "Tabel kondisi perusahaan", Icons.Default.Timeline, Routes.KESIMPULAN),
    FiturLink("Notifikasi CbCR", "Pengingat 12 bulan setelah tahun pajak", Icons.Default.FactCheck, Routes.NOTIFIKASI),
    FiturLink("Pencarian", "Cari seluruh materi", Icons.Default.Search, Routes.CARI),
)

@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit,
    onBukaTab: (String) -> Unit,
    isTabActive: Boolean,
) {
    ScreenScaffold(
        title = "TP Doc Indonesia",
        onBack = null,
        bottomBar = null,
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Default.Home, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                    Text(
                        "Transfer Pricing Document",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
                Text(
                    ContentData.pengertianTP,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }

        InfoCard(
            title = "Prinsip Kewajaran & Kelaziman Usaha",
            body = ContentData.prinsipKewajaran,
            icon = Icons.Default.Info,
        )

        SectionHeader("Dasar Hukum")
        ContentData.dasarHukum.forEach { (aturan, judul) ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(aturan, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text(judul, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        SectionHeader("Fitur")
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(fitur) { f ->
                Card(
                    onClick = { onNavigate(f.route) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(f.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(f.label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Text(f.deskripsi, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}