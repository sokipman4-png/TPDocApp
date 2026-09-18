package com.tpdoc.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tpdoc.app.calc.BerelasiChecker
import com.tpdoc.app.ui.components.ChecklistGroup
import com.tpdoc.app.ui.components.InfoCard
import com.tpdoc.app.ui.components.ScreenScaffold
import com.tpdoc.app.ui.components.SectionHeader
import com.tpdoc.app.ui.viewmodel.ChecklistViewModel

@Composable
fun BerelasiScreen(onBack: () -> Unit) {
    val vm: ChecklistViewModel = viewModel()
    val items by vm.berelasi.collectAsState(initial = emptyList())
    val adaIndikasi = BerelasiChecker.terindikasiBerelasi(items.map { it.checked })

    ScreenScaffold(title = "Pihak Berelasi", onBack = onBack) {
        Text(
            "Seseorang/badan dianggap pihak berelasi (hubungan istimewa) jika memenuhi salah satu indikator berikut. Centang yang berlaku pada perusahaan Anda.",
            style = MaterialTheme.typography.bodyMedium,
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (adaIndikasi) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
            ),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(
                    if (adaIndikasi) Icons.Default.Group else Icons.Default.Shield,
                    contentDescription = null,
                    tint = if (adaIndikasi) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    if (adaIndikasi) "TERINDIKASI PIHAK BERELASI" else "Belum ada indikator tercentang",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (adaIndikasi) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    if (adaIndikasi) "Transaksi dengan pihak ini tunduk pada prinsip kewajaran Transfer Pricing." else "Jika seluruh kondisi di atas TIDAK terpenuhi, pihak dianggap tidak berelasi secara formal.",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (adaIndikasi) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        ChecklistGroup(
            title = "Indikator Hubungan Istimewa",
            items = items,
            onToggle = vm::toggle,
        )

        SectionHeader("Catatan Hubungan Usaha")
        InfoCard(
            title = "Ketergantungan usaha",
            body = "Hubungan usaha berupa ketergantungan keuangan atau teknis juga termasuk indikator berelasi.",
            icon = Icons.Default.TrendingUp,
        )
    }
}