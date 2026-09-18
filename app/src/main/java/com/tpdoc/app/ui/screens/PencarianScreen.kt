package com.tpdoc.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tpdoc.app.data.ContentData
import com.tpdoc.app.ui.components.ScreenScaffold
import com.tpdoc.app.ui.viewmodel.PencarianViewModel

@Composable
fun PencarianScreen(onBack: () -> Unit, onNavigate: (String) -> Unit) {
    val vm: PencarianViewModel = viewModel()
    val hasil = vm.hasil()

    ScreenScaffold(title = "Pencarian Materi", onBack = onBack) {
        OutlinedTextField(
            value = vm.query,
            onValueChange = vm::updateQuery,
            label = { Text("Cari materi (mis. cbcr, threshold, royalti...)") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        if (vm.query.isNotBlank() && hasil.isEmpty()) {
            Text(
                "Tidak ada materi yang cocok dengan \"${vm.query}\".",
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        hasil.forEach { item ->
            Card(
                onClick = { onNavigate(item.route) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            item.section,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                        )
                        Text("•", color = MaterialTheme.colorScheme.outline)
                        Text(item.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    }
                    Text(
                        item.body.take(140) + (if (item.body.length > 140) "..." else ""),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}