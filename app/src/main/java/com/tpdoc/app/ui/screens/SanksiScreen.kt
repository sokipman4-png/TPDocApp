package com.tpdoc.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tpdoc.app.data.ContentData
import com.tpdoc.app.ui.components.ScreenScaffold
import com.tpdoc.app.ui.components.WarningBanner

@Composable
fun SanksiScreen(onBack: () -> Unit) {
    ScreenScaffold(title = "Sanksi", onBack = onBack) {
        Text(
            "Sanksi jika tidak patuh terhadap kewajiban TP Doc:",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
        )
        ContentData.sanksi.forEach { s ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    androidx.compose.foundation.layout.Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Default.Gavel, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Text(s.nama, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }
                    Text(s.keterangan, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        WarningBanner("Pencegahan terbaik: terapkan prinsip kewajaran, lengkapi dokumentasi, dan penuhi batas waktu pelaporan (termasuk notifikasi CbCR maksimal 12 bulan setelah akhir tahun pajak).")
    }
}