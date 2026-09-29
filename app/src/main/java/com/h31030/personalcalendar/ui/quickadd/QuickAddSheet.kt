package com.h31030.personalcalendar.ui.quickadd

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.h31030.personalcalendar.data.local.entity.CategoryEntity
import com.h31030.personalcalendar.data.local.entity.TransactionType

private enum class QuickAddType { EVENT, TODO, DIARY, BUDGET }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAddSheet(
    quickAccessCategories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onAddEvent: (title: String) -> Unit,
    onAddTodo: (title: String) -> Unit,
    onSaveDiary: (content: String) -> Unit,
    onAddTransaction: (category: CategoryEntity, amount: Int) -> Unit,
) {
    var activeType by remember { mutableStateOf<QuickAddType?>(null) }
    var selectedCategory by remember { mutableStateOf<CategoryEntity?>(null) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(bottom = 24.dp)) {
            Text(
                text = "クイック追加",
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
                style = MaterialTheme.typography.titleMedium,
            )
            ListItem(
                headlineContent = { Text("予定") },
                leadingContent = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                modifier = Modifier.clickable { activeType = QuickAddType.EVENT },
            )
            ListItem(
                headlineContent = { Text("Todo") },
                leadingContent = { Icon(Icons.Default.CheckBox, contentDescription = null) },
                modifier = Modifier.clickable { activeType = QuickAddType.TODO },
            )
            ListItem(
                headlineContent = { Text("日記") },
                leadingContent = { Icon(Icons.Default.Book, contentDescription = null) },
                modifier = Modifier.clickable { activeType = QuickAddType.DIARY },
            )
            ListItem(
                headlineContent = { Text("家計簿") },
                leadingContent = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null) },
                modifier = Modifier.clickable { activeType = QuickAddType.BUDGET },
            )
        }
    }

    when (activeType) {
        QuickAddType.EVENT -> QuickTextDialog(
            title = "予定をクイック追加",
            label = "タイトル",
            onConfirm = { onAddEvent(it); activeType = null; onDismiss() },
            onDismiss = { activeType = null },
        )
        QuickAddType.TODO -> QuickTextDialog(
            title = "Todoをクイック追加",
            label = "タイトル",
            onConfirm = { onAddTodo(it); activeType = null; onDismiss() },
            onDismiss = { activeType = null },
        )
        QuickAddType.DIARY -> QuickTextDialog(
            title = "今日の日記",
            label = "今日あったこと",
            singleLine = false,
            onConfirm = { onSaveDiary(it); activeType = null; onDismiss() },
            onDismiss = { activeType = null },
        )
        QuickAddType.BUDGET -> {
            if (selectedCategory == null) {
                QuickCategoryPickerDialog(
                    categories = quickAccessCategories,
                    onSelect = { selectedCategory = it },
                    onDismiss = { activeType = null },
                )
            } else {
                QuickAmountDialog(
                    category = selectedCategory!!,
                    onConfirm = { amount ->
                        onAddTransaction(selectedCategory!!, amount)
                        selectedCategory = null
                        activeType = null
                        onDismiss()
                    },
                    onDismiss = { selectedCategory = null; activeType = null },
                )
            }
        }
        null -> Unit
    }
}

@Composable
private fun QuickTextDialog(
    title: String,
    label: String,
    singleLine: Boolean = true,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text(label) },
                singleLine = singleLine,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(text) }, enabled = text.isNotBlank()) { Text("保存") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("キャンセル") }
        },
    )
}

@Composable
private fun QuickCategoryPickerDialog(
    categories: List<CategoryEntity>,
    onSelect: (CategoryEntity) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("費目を選択") },
        text = {
            if (categories.isEmpty()) {
                Text("クイック入力用の費目が設定されていません。設定画面から登録してください。")
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.height(160.dp),
                ) {
                    items(categories) { category ->
                        OutlinedButton(
                            onClick = { onSelect(category) },
                            modifier = Modifier.padding(4.dp),
                        ) {
                            Text(category.name)
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("キャンセル") }
        },
    )
}

@Composable
private fun QuickAmountDialog(category: CategoryEntity, onConfirm: (Int) -> Unit, onDismiss: () -> Unit) {
    var amountText by remember { mutableStateOf("") }
    val typeLabel = if (category.type == TransactionType.EXPENSE) "支出" else "収入"
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("${category.name}（$typeLabel）") },
        text = {
            OutlinedTextField(
                value = amountText,
                onValueChange = { input -> amountText = input.filter(Char::isDigit) },
                label = { Text("金額（円）") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            Button(
                onClick = { amountText.toIntOrNull()?.let(onConfirm) },
                enabled = amountText.toIntOrNull()?.let { it > 0 } == true,
            ) { Text("保存") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("キャンセル") }
        },
    )
}

