package com.tpdoc.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tpdoc.app.calc.NotifikasiCalculator
import com.tpdoc.app.data.ContentData
import com.tpdoc.app.ui.components.InfoCard
import com.tpdoc.app.ui.components.ScreenScaffold
import com.tpdoc.app.ui.viewmodel.NotifikasiViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotifikasiScreen(onBack: () -> Unit) {
    val vm: NotifikasiViewModel = viewModel()
    val reminder: NotifikasiCalculator.Reminder = vm.reminder()

    ScreenScaffold(title = "Notifikasi CbCR", onBack = onBack) {
        InfoCard(
            title = "Apa itu Notifikasi CbCR?",
            body = ContentData.notifikasiCbcr,
            icon = Icons.Default.NotificationsActive,
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = when (reminder.status) {
                    NotifikasiCalculator.Status.TERLAMBAT -> MaterialTheme.colorScheme.errorContainer
                    NotifikasiCalculator.Status.SEGERA -> MaterialTheme.colorScheme.secondaryContainer
                    else -> MaterialTheme.colorScheme.primaryContainer
                },
            ),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(
                        "Tahun pajak: ${reminder.fiscalYear}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Text("Akhir tahun pajak: ${fmt(reminder.endOfTaxYear.time)}")
                Text("Batas notifikasi CbCR: ${fmt(reminder.dueDate.time)}")
                Text(
                    when (reminder.status) {
                        NotifikasiCalculator.Status.TERLAMBAT ->
                            "TERLAMBAT ${-reminder.daysRemaining} hari — segera koordinasikan dengan konsultan pajak."
                        NotifikasiCalculator.Status.SEGERA ->
                            "Hanya ${reminder.daysRemaining} hari lagi! Segera sampaikan notifikasi secara elektronik."
                        NotifikasiCalculator.Status.MENDEKAT ->
                            "Sisa ${reminder.daysRemaining} hari (kurang dari 6 bulan). Siapkan notifikasi CbCR."
                        NotifikasiCalculator.Status.JAUH ->
                            "Masih ${reminder.daysRemaining} hari menuju batas waktu. Notifikasi maksimal 12 bulan setelah akhir tahun pajak."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Set Tahun Pajak", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = vm.inputTahun,
                    onValueChange = vm::updateInput,
                    label = { Text("Tahun pajak (mis. 2024)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
                Button(onClick = vm::simpan, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Event, contentDescription = null)
                    Text("  Simpan Tahun Pajak")
                }
            }
        }
    }
}

private fun fmt(date: Date): String =
    SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(date)