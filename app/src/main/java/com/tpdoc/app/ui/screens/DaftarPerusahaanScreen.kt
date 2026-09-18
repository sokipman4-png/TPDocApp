package com.tpdoc.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tpdoc.app.data.room.Perusahaan
import com.tpdoc.app.ui.components.ScreenScaffold
import com.tpdoc.app.ui.viewmodel.PerusahaanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaftarPerusahaanScreen(
    onTambah: () -> Unit,
    onDetail: (Long) -> Unit,
    onBack: (() -> Unit)? = null,
    vm: PerusahaanViewModel = viewModel(),
) {
    val state by vm.uiState.collectAsState()
    val list by vm.listFlow.collectAsState()
    var showDeleteDialog by remember { mutableStateOf<Perusahaan?>(null) }
    var showFilterMenu by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    ScreenScaffold(
        title = "Daftar Perusahaan",
        onBack = onBack,
        actions = {
            IconButton(onClick = { showFilterMenu = true }) {
                Icon(Icons.Default.FilterList, contentDescription = "Filter")
            }
            DropdownMenu(expanded = showFilterMenu, onDismissRequest = { showFilterMenu = false }) {
                DropdownMenuItem(
                    text = { Text("Semua Status") },
                    onClick = { vm.clearFilters(); showFilterMenu = false },
                )
                Perusahaan.STATUSES.forEach { s ->
                    DropdownMenuItem(
                        text = { Text(s.replaceFirstChar { it.uppercase() }) },
                        onClick = { vm.setFilter(s, state.filterNegara); showFilterMenu = false },
                    )
                }
            }
            if (state.isSearching || state.filterStatus != null || state.filterNegara != null) {
                IconButton(onClick = { vm.clearSearch(); vm.clearFilters(); searchText = "" }) {
                    Icon(Icons.Default.Clear, contentDescription = "Hapus filter")
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onTambah) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Perusahaan")
            }
        },
    ) {
        Column {
            // Search bar
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it; vm.search(it) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Cari nama, NPWP, status, negara...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchText.isNotBlank()) {
                        IconButton(onClick = { searchText = ""; vm.clearSearch() }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
            )

            // Filter chips indicator
            if (state.filterStatus != null || state.filterNegara != null) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (state.filterStatus != null) {
                        Text(
                            "Status: ${state.filterStatus}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    if (state.filterNegara != null) {
                        Text(
                            "Negara: ${state.filterNegara}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
            }

            // Count
            Text(
                "${list.size} perusahaan",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (list.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        if (state.isSearching) "Tidak ada hasil" else "Belum ada perusahaan. Klik + untuk tambah.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(list, key = { it.id }) { p ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .clickable { onDetail(p.id) },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        p.nama,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                    Text(
                                        "NPWP: ${p.npwp}  |  ${p.status.replaceFirstChar { it.uppercase() }}  |  ${p.negara}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                    Text(
                                        "Tahun Pajak: ${p.tahunPajak}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                IconButton(onClick = { vm.duplicate(p) }) {
                                    Icon(
                                        Icons.Default.ContentCopy,
                                        contentDescription = "Duplikat",
                                        tint = MaterialTheme.colorScheme.primary,
                                    )
                                }
                                IconButton(onClick = { showDeleteDialog = p }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Hapus",
                                        tint = MaterialTheme.colorScheme.error,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    showDeleteDialog?.let { p ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Hapus ${p.nama}?") },
            text = { Text("Data perusahaan beserta hierarkinya akan dihapus. Tindakan ini tidak bisa dibatalkan.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.deleteById(p.id)
                        showDeleteDialog = null
                    },
                ) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Batal")
                }
            },
        )
    }
}