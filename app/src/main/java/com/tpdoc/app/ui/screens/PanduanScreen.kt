package com.tpdoc.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tpdoc.app.data.content.PanduanContent
import com.tpdoc.app.ui.components.HelpButton
import com.tpdoc.app.ui.components.ScreenScaffold

/**
 * Halaman tutorial/panduan in-app (STEP 5.B Tahap 2.6).
 * Content komt uit PanduanContent (pure, getest). Bereikbaar via
 * Pengaturan → "Panduan Penggunaan".
 */
@Composable
fun PanduanScreen(onBack: () -> Unit) {
    ScreenScaffold(
        title = "Panduan Penggunaan",
        onBack = onBack,
        actions = {
            HelpButton(
                "Panduan compleet voor gebruikers van TP Doc Manager: van het toevoegen van bedrijven " +
                    "en hiërarchie tot AI-analyse, API key, export/share en backup/restore.",
                contentDescription = "Panduan over deze pagina",
            )
        },
    ) {
        Text(
            "Hoe gebruik ik TP Doc Manager? Onderstaande stappen leggen elke functie uit.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        PanduanContent.sections.forEach { section ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(section.judul, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text(section.body, style = MaterialTheme.typography.bodyMedium)
                    section.bullets.forEach { bullet ->
                        Text("•  $bullet", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        // FAQ
        Text("FAQ — Pertanyaan saat ini", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        PanduanContent.faq.forEach { faq ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(faq.pertanyaan, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    }
                    Text(faq.jawaban, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Text(
            "API key aanmaken: " + PanduanContent.URL_OPENROUTER_KEYS,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}