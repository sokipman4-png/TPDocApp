package com.tpdoc.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tpdoc.app.data.room.Perusahaan
import com.tpdoc.app.ui.viewmodel.FormPerusahaanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormPerusahaanScreen(
    perusahaanId: Long? = null,
    onSaved: () -> Unit,
    onBack: () -> Unit,
    vm: FormPerusahaanViewModel = viewModel(),
) {
    val state by vm.uiState.collectAsState()

    LaunchedEffect(perusahaanId) {
        if (perusahaanId != null && perusahaanId > 0) {
            vm.loadForEdit(perusahaanId)
        } else {
            vm.loadDaftarInduk()
        }
    }

    LaunchedEffect(state.saved) {
        if (state.saved) onSaved()
    }

    Column {
        TopAppBar(
            title = { Text(if (state.isEdit) "Edit Perusahaan" else "Tambah Perusahaan") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                }
            },
        )

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Nama
            OutlinedTextField(
                value = state.nama,
                onValueChange = { vm.updateNama(it) },
                label = { Text("Nama PT *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = state.error != null && state.nama.isBlank(),
            )

            // NPWP
            OutlinedTextField(
                value = state.npwp,
                onValueChange = { vm.updateNpwp(it) },
                label = { Text("NPWP") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )

            // Alamat
            OutlinedTextField(
                value = state.alamat,
                onValueChange = { vm.updateAlamat(it) },
                label = { Text("Alamat") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
            )

            // Negara
            OutlinedTextField(
                value = state.negara,
                onValueChange = { vm.updateNegara(it) },
                label = { Text("Negara") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            // Status dropdown
            StatusDropdown(
                selected = state.status,
                onSelect = { vm.updateStatus(it) },
                modifier = Modifier.fillMaxWidth(),
            )

            // Parent (induk) dropdown
            if (state.daftarInduk.isNotEmpty() && state.status != Perusahaan.STATUS_INDUK) {
                IndukDropdown(
                    selected = state.parentId,
                    options = state.daftarInduk,
                    onSelect = { vm.updateParentId(it) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // Tahun Pajak
            OutlinedTextField(
                value = state.tahunPajak.toString(),
                onValueChange = { v -> v.toIntOrNull()?.let { vm.updateTahunPajak(it) } },
                label = { Text("Tahun Pajak") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )

            // Error
            state.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { vm.save() },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (state.isEdit) "Simpan Perubahan" else "Simpan Perusahaan")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatusDropdown(selected: String, onSelect: (String) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }, modifier = modifier) {
        OutlinedTextField(
            value = selected.replaceFirstChar { it.uppercase() },
            onValueChange = {},
            readOnly = true,
            label = { Text("Status") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            Perusahaan.STATUSES.forEach { s ->
                DropdownMenuItem(
                    text = { Text(s.replaceFirstChar { it.uppercase() }) },
                    onClick = { onSelect(s); expanded = false },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IndukDropdown(
    selected: Long?,
    options: List<Perusahaan>,
    onSelect: (Long?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedName = options.find { it.id == selected }?.nama ?: "Tidak ada"
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }, modifier = modifier) {
        OutlinedTextField(
            value = selectedName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Induk Perusahaan") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("Tidak ada (root)") },
                onClick = { onSelect(null); expanded = false },
            )
            options.forEach { p ->
                DropdownMenuItem(
                    text = { Text(p.nama) },
                    onClick = { onSelect(p.id); expanded = false },
                )
            }
        }
    }
}