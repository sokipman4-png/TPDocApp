package com.tpdoc.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class AnalisisComponent(
    val id: String,
    val label: String,
    val enabled: Boolean = true,
)

data class CostEstimate(
    val inputTokens: Int = 0,
    val outputTokens: Int = 0,
    val promptPrice: Double = 0.0,
    val completionPrice: Double = 0.0,
    val totalUsd: Double = 0.0,
    val exchangeRate: Float = 16000f,
) {
    val totalIdr: Long get() = (totalUsd * exchangeRate).toLong()
    val inputUsd: Double get() = inputTokens / 1_000_000.0 * promptPrice
    val outputUsd: Double get() = outputTokens / 1_000_000.0 * completionPrice
}

@Composable
fun AIAnalisisDialog(
    components: List<AnalisisComponent>,
    estimate: CostEstimate,
    onComponentToggle: (Int) -> Unit,
    onSelectAll: () -> Unit,
    onDeselectAll: () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Konfirmasi Analisis AI") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Component checklist
                Text("Komponen Analisis:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                components.forEachIndexed { i, comp ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = comp.enabled,
                            onCheckedChange = { onComponentToggle(i) },
                        )
                        Text(comp.label, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                // Select/Desellect All
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = onSelectAll) { Text("Pilih Semua") }
                    TextButton(onClick = onDeselectAll) { Text("Hapus Semua") }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                // Cost estimate
                Text("Estimasi Biaya:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Text("Input token: ${estimate.inputTokens}", style = MaterialTheme.typography.bodySmall)
                Text("Output token: ${estimate.outputTokens}", style = MaterialTheme.typography.bodySmall)
                Text("Harga input/1jt: \$${String.format("%.4f", estimate.promptPrice)}", style = MaterialTheme.typography.bodySmall)
                Text("Harga output/1jt: \$${String.format("%.4f", estimate.completionPrice)}", style = MaterialTheme.typography.bodySmall)
                Text("Total: \$${String.format("%.6f", estimate.totalUsd)} (Rp ${formatRupiah(estimate.totalIdr)})",
                    style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                Text(
                    "Disclaimer: Estimasi ini perkiraan. Biaya aktual bisa berbeda.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Lanjut Analisis") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        },
    )
}

fun formatRupiah(amount: Long): String {
    return "%,d".format(amount).replace(',', '.')
}