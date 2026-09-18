package com.tpdoc.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tpdoc.app.data.ContentData
import com.tpdoc.app.ui.components.ScreenScaffold
import com.tpdoc.app.ui.components.SectionHeader
import com.tpdoc.app.ui.components.WarningBanner

@Composable
fun TransaksiScreen(onBack: () -> Unit) {
    ScreenScaffold(title = "Jenis Transaksi", onBack = onBack) {
        SectionHeader("Transaksi yang Kena Transfer Pricing")
        Text(
            "Paling sering terjadi di holding & grup usaha:",
            style = MaterialTheme.typography.bodyMedium,
        )
        ContentData.jenisTransaksi.forEachIndexed { i, t ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    androidx.compose.foundation.layout.Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Default.CurrencyExchange, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            "${i + 1}. ${t.nama}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Text(t.keterangan, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        WarningBanner("Semua jenis transaksi di atas wajib mengikuti prinsip kewajaran (ALP) dan dapat dikenai koreksi jika tidak wajar.")
    }
}