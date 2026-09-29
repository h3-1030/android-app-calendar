package com.h31030.personalcalendar.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.h31030.personalcalendar.R
import com.h31030.personalcalendar.data.local.entity.CategoryEntity
import com.h31030.personalcalendar.data.local.entity.TransactionType
import com.h31030.personalcalendar.ui.LocalAppContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val container = LocalAppContainer.current
    val viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.factory(container.categoryRepository))
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.nav_settings)) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "カテゴリを追加")
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Text(text = "家計簿カテゴリ", style = MaterialTheme.typography.titleMedium)
            }
            item {
                Text(
                    text = "「クイック表示」をONにしたカテゴリが、クイック追加の費目ボタンに表示されます。",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            items(categories, key = { it.id }) { category ->
                CategoryRow(
                    category = category,
                    onToggleQuickAccess = { viewModel.toggleQuickAccess(category) },
                    onDelete = { viewModel.delete(category) },
                )
            }
        }
    }

    if (showAddDialog) {
        AddCategoryDialog(
            onConfirm = { name, type -> viewModel.addCategory(name, type); showAddDialog = false },
            onDismiss = { showAddDialog = false },
        )
    }
}

@Composable
private fun CategoryRow(category: CategoryEntity, onToggleQuickAccess: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = category.name, style = MaterialTheme.typography.bodyLarge)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                FilterChip(
                    selected = category.type == TransactionType.EXPENSE,
                    onClick = {},
                    enabled = false,
                    label = { Text(if (category.type == TransactionType.EXPENSE) "支出" else "収入") },
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "クイック表示", style = MaterialTheme.typography.labelSmall)
                Switch(checked = category.isQuickAccess, onCheckedChange = { onToggleQuickAccess() })
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.action_delete))
                }
            }
        }
    }
}

@Composable
private fun AddCategoryDialog(onConfirm: (String, TransactionType) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(TransactionType.EXPENSE) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("カテゴリを追加") },
        text = {
            androidx.compose.foundation.layout.Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("カテゴリ名") })
                Row(modifier = Modifier.padding(top = 8.dp)) {
                    FilterChip(
                        selected = type == TransactionType.EXPENSE,
                        onClick = { type = TransactionType.EXPENSE },
                        label = { Text("支出") },
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    FilterChip(
                        selected = type == TransactionType.INCOME,
                        onClick = { type = TransactionType.INCOME },
                        label = { Text("収入") },
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(name, type) }, enabled = name.isNotBlank()) {
                Text(stringResource(R.string.action_save))
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) } },
    )
}
