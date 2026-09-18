package com.tpdoc.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tpdoc.app.ui.components.ScreenScaffold
import com.tpdoc.app.ui.viewmodel.DummyDataViewModel
import com.tpdoc.app.ui.viewmodel.OnboardingFlow
import com.tpdoc.app.ui.viewmodel.OnboardingUiState

private data class Slide(val icon: ImageVector, val body: String)

private val slides = listOf(
    Slide(
        Icons.Default.Home,
        "Welkom bij TP Doc Manager — uw lokale begeleider voor transfer pricing in Indonesië. " +
            "Beheer bedrijven, bereken drempels (thresholds), volg documentatie en analyseer met of zonder AI.",
    ),
    Slide(
        Icons.Default.Group,
        "Beheer meerdere bedrijven in één groep met een hiërarchie: induk (moeder), anak (kind), " +
            "cucu (kleinkind) en cabang (filiaal). Elk bedrijf heeft zijn eigen NPWP, status en belastingjaar.",
    ),
    Slide(
        Icons.Default.Calculate,
        "Analyseer elk bedrijf in twee modi: Non-AI (drempelcalculator + checklist + sanctieschatting, " +
            "gratis en offline) en AI (diepgaande analyse via OpenRouter — vereist een API key en toont eerst de kosten).",
    ),
    Slide(
        Icons.Default.Description,
        "U bent klaar! Start direct, of laad eerst drie voorbeeldbedrijven in een hiërarchie " +
            "(2 belastingjaren) om de app te leren kennen.",
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    onSkip: (() -> Unit)? = null,
    dummyVm: DummyDataViewModel = viewModel(),
) {
    var flow by remember { mutableStateOf(OnboardingUiState()) }
    val dummyState by dummyVm.uiState.collectAsState()

    // Bevestigingsdialog data contoh
    if (dummyState.showConfirm) {
        AlertDialog(
            onDismissRequest = { dummyVm.cancelGenerate() },
            title = { Text("Muat Data Contoh") },
            text = {
                Text(
                    "Data contoh akan ditambahkan (3 perusahaan, 2 tahun pajak, hiërarchie induk-anak-cucu).\n\n" +
                        "Lanjut? Data existing zal niet worden verwijderd.",
                )
            },
            confirmButton = {
                TextButton(onClick = { dummyVm.generate { onFinish() } }) { Text("Lanjut") }
            },
            dismissButton = {
                TextButton(onClick = { dummyVm.cancelGenerate() }) { Text("Batal") }
            },
        )
    }

    // Snackbar/error data contoh
    dummyState.snackbar?.let { msg ->
        AlertDialog(
            onDismissRequest = { dummyVm.dismiss() },
            title = { Text("Sukses") },
            text = { Text(msg) },
            confirmButton = { TextButton(onClick = { dummyVm.dismiss() }) { Text("OK") } },
        )
    }
    dummyState.error?.let { msg ->
        AlertDialog(
            onDismissRequest = { dummyVm.dismiss() },
            title = { Text("Error") },
            text = { Text(msg) },
            confirmButton = { TextButton(onClick = { dummyVm.dismiss() }) { Text("OK") } },
        )
    }

    val slide = slides[minOf(flow.page, slides.size - 1)]
    val isLast = OnboardingFlow.isLast(flow)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Boas Vindas") },
                actions = {
                    TextButton(onClick = {
                        flow = flow.copy(done = true)
                        if (onSkip != null) onSkip() else onFinish()
                    }) {
                        Text("Lewati", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // Illustratie
            Icon(
                slide.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(96.dp),
            )

            Text(
                OnboardingFlow.pageTitles().get(flow.page),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Text(
                slide.body,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            // Pagina-indicator (dots)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (i in 0..OnboardingFlow.TOTAL_PAGES - 1) {
                    val active = i == flow.page
                    Icon(
                        if (active) Icons.Default.Star else Icons.Default.Circle,
                        contentDescription = null,
                        tint = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(if (active) 14.dp else 10.dp),
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            if (isLast) {
                Button(onClick = {
                    flow = OnboardingFlow.finish(flow)
                    onFinish()
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Mulai Sekarang")
                }
                OutlinedButton(onClick = { dummyVm.askGenerate() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Muat Data Contoh")
                }
            } else {
                Button(onClick = {
                    flow = OnboardingFlow.next(flow)
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Lanjut")
                }
            }
        }
    }
}