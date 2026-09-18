package com.tpdoc.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.Card
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
import com.tpdoc.app.calc.ThresholdCalculator
import com.tpdoc.app.ui.components.ScreenScaffold
import com.tpdoc.app.ui.components.SectionHeader
import com.tpdoc.app.ui.components.StatusCard
import com.tpdoc.app.ui.components.WarningBanner
import com.tpdoc.app.ui.viewmodel.KalkulatorViewModel

@Composable
fun KalkulatorScreen(onBack: () -> Unit) {
    val vm: KalkulatorViewModel = viewModel()

    ScreenScaffold(title = "Kalkulator Threshold", onBack = onBack) {
        Text(
            "Cek kewajiban dokumen TP Doc berdasarkan omzet konsolidasi grup dan nilai transaksi afiliasi.",
            style = MaterialTheme.typography.bodyMedium,
        )

        OutlinedTextField(
            value = vm.inputOmzet,
            onValueChange = vm::updateOmzet,
            label = { Text("Omzet Konsolidasi Grup (Rp)") },
            prefix = { Text("Rp ") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            "Terbaca: ${KalkulatorViewModel.formatRupiah(vm.omzet)}",
            style = MaterialTheme.typography.labelMedium,
        )

        OutlinedTextField(
            value = vm.inputTransaksi,
            onValueChange = vm::updateTransaksi,
            label = { Text("Total Transaksi Afiliasi (Rp)") },
            prefix = { Text("Rp ") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            "Terbaca: ${KalkulatorViewModel.formatRupiah(vm.transaksi)}",
            style = MaterialTheme.typography.labelMedium,
        )

        val hasil: ThresholdCalculator.Result = vm.hasil

        SectionHeader("Hasil")
        StatusCard(
            wajib = hasil.wajibMasterLocal,
            judul = "Master File + Local File",
            alasan = hasil.alasanMasterLocal,
        )
        StatusCard(
            wajib = hasil.wajibCbcr,
            judul = "CbCR + Notifikasi CbCR",
            alasan = hasil.alasanCbcr,
        )

        WarningBanner("Walau belum melewati threshold, prinsip kewajaran & kelaziman usaha tetap wajib. DJP tetap bisa koreksi kalau harga tidak wajar.")

        SectionHeader("Aturan Threshold")
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Calculate, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Batasan yang dipakai", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
                Text("•  Master File + Local File: omzet grup > Rp50 M DAN transaksi afiliasi > Rp20 M")
                Text("•  CbCR: omzet konsolidasi grup > Rp11 T")
                Text(
                    "•  Batas memakai \"lebih dari\" (>): tepat Rp50 M / Rp20 M / Rp11 T belum wajib formal.",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}