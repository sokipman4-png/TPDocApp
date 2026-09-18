package com.tpdoc.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tpdoc.app.data.ContentData
import com.tpdoc.app.ui.components.ScreenScaffold
import com.tpdoc.app.ui.components.WarningBanner

@Composable
fun KesimpulanScreen(onBack: () -> Unit) {
    ScreenScaffold(title = "Kesimpulan", onBack = onBack) {
        Text(
            "Ringkasan kondisi perusahaan terhadap kewajiban TP Doc:",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
        )

        // Header tabel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp))
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                "Kondisi Perusahaan",
                modifier = Modifier.weight(1.3f),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
            )
            Text(
                "Wajib TP",
                modifier = Modifier.weight(0.9f),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
            )
            Text(
                "Wajib Dokumen",
                modifier = Modifier.weight(1.1f),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
            )
        }

        ContentData.kesimpulan.forEach { row ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(row.kondisi, modifier = Modifier.weight(1.3f), style = MaterialTheme.typography.bodySmall)
                    Text(
                        row.wajibTp,
                        modifier = Modifier.weight(0.9f),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(row.wajibDokumen, modifier = Modifier.weight(1.1f), style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        WarningBanner("Tabel ini ringkasan edukatif dari dokumen TP Doc; keputusan final tetap mengacu pada PMK 172/2023, PER-22/PJ/2023, dan ketentuan DJP terkait.")
    }
}