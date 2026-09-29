package com.h31030.personalcalendar.ui.daydetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.h31030.personalcalendar.data.local.entity.CategoryEntity
import com.h31030.personalcalendar.data.local.entity.EventEntity
import com.h31030.personalcalendar.data.local.entity.TodoEntity
import com.h31030.personalcalendar.data.local.entity.TransactionEntity
import com.h31030.personalcalendar.data.local.entity.TransactionType
import com.h31030.personalcalendar.ui.LocalAppContainer
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayDetailScreen(date: LocalDate, onBack: () -> Unit) {
    val container = LocalAppContainer.current
    val viewModel: DayDetailViewModel = viewModel(
        key = date.toString(),
        factory = DayDetailViewModel.factory(
            date,
            container.eventRepository,
            container.todoRepository,
            container.diaryRepository,
            container.transactionRepository,
            container.categoryRepository,
        ),
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showAddEvent by remember { mutableStateOf(false) }
    var showAddTodo by remember { mutableStateOf(false) }
    var showAddTransaction by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(date.format(DateTimeFormatter.ofPattern("yyyy年M月d日"))) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "戻る")
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                SectionHeader(title = "予定", onAddClick = { showAddEvent = true })
            }
            items(uiState.events) { event ->
                EventRow(event = event, onDelete = { viewModel.deleteEvent(event) })
            }

            item {
                SectionHeader(title = "Todo", onAddClick = { showAddTodo = true })
            }
            items(uiState.todos) { todo ->
                TodoRow(
                    todo = todo,
                    onToggle = { viewModel.toggleTodoDone(todo) },
                    onDelete = { viewModel.deleteTodo(todo) },
                )
            }

            item {
                SectionHeader(title = "日記", onAddClick = null)
                DiarySection(content = uiState.diary?.content.orEmpty(), onSave = viewModel::saveDiary)
            }

            item {
                SectionHeader(title = "家計簿", onAddClick = { showAddTransaction = true })
            }
            items(uiState.transactions) { transaction ->
                val category = uiState.categories.find { it.id == transaction.categoryId }
                TransactionRow(
                    transaction = transaction,
                    categoryName = category?.name ?: "(削除済みカテゴリ)",
                    onDelete = { viewModel.deleteTransaction(transaction) },
                )
            }
        }
    }

    if (showAddEvent) {
        AddEventDialog(
            onConfirm = { title, memo -> viewModel.addEvent(title, memo); showAddEvent = false },
            onDismiss = { showAddEvent = false },
        )
    }
    if (showAddTodo) {
        AddTodoDialog(
            onConfirm = { title, dueDate -> viewModel.addTodo(title, dueDate); showAddTodo = false },
            onDismiss = { showAddTodo = false },
        )
    }
    if (showAddTransaction) {
        AddTransactionDialog(
            categories = uiState.categories,
            onConfirm = { category, amount, memo ->
                viewModel.addTransaction(category, amount, memo)
                showAddTransaction = false
            },
            onDismiss = { showAddTransaction = false },
        )
    }
}

@Composable
private fun SectionHeader(title: String, onAddClick: (() -> Unit)?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        if (onAddClick != null) {
            IconButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "$title を追加")
            }
        }
    }
}

@Composable
private fun EventRow(event: EventEntity, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(text = event.title, style = MaterialTheme.typography.bodyLarge)
                if (event.memo.isNotBlank()) {
                    Text(text = event.memo, style = MaterialTheme.typography.bodySmall)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "削除")
            }
        }
    }
}

@Composable
private fun TodoRow(todo: TodoEntity, onToggle: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row {
                Checkbox(checked = todo.isDone, onCheckedChange = { onToggle() })
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = if (todo.isDone) TextDecoration.LineThrough else null,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "削除")
            }
        }
    }
}

@Composable
private fun DiarySection(content: String, onSave: (String) -> Unit) {
    var text by remember(content) { mutableStateOf(content) }
    Column {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("数行でその日のことを書く") },
            minLines = 3,
        )
        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
            TextButton(onClick = { onSave(text) }, enabled = text != content) {
                Text("保存")
            }
        }
    }
}

@Composable
private fun TransactionRow(transaction: TransactionEntity, categoryName: String, onDelete: () -> Unit) {
    val sign = if (transaction.type == TransactionType.EXPENSE) "-" else "+"
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(text = categoryName, style = MaterialTheme.typography.bodyLarge)
                if (transaction.memo.isNotBlank()) {
                    Text(text = transaction.memo, style = MaterialTheme.typography.bodySmall)
                }
            }
            Row {
                Text(text = "$sign${transaction.amount}円", style = MaterialTheme.typography.bodyLarge)
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "削除")
                }
            }
        }
    }
}

@Composable
private fun AddEventDialog(onConfirm: (title: String, memo: String) -> Unit, onDismiss: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var memo by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("予定を追加") },
        text = {
            Column {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("タイトル") })
                OutlinedTextField(value = memo, onValueChange = { memo = it }, label = { Text("メモ（任意）") })
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(title, memo) }, enabled = title.isNotBlank()) { Text("保存") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("キャンセル") } },
    )
}

@Composable
private fun AddTodoDialog(onConfirm: (title: String, dueDate: LocalDate?) -> Unit, onDismiss: () -> Unit) {
    var title by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Todoを追加") },
        text = {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("タイトル") })
        },
        confirmButton = {
            Button(onClick = { onConfirm(title, null) }, enabled = title.isNotBlank()) { Text("保存") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("キャンセル") } },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTransactionDialog(
    categories: List<CategoryEntity>,
    onConfirm: (category: CategoryEntity, amount: Int, memo: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull()) }
    var amountText by remember { mutableStateOf("") }
    var memo by remember { mutableStateOf("") }

    LaunchedEffect(categories) {
        if (selectedCategory == null) selectedCategory = categories.firstOrNull()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("家計簿明細を追加") },
        text = {
            Column {
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value = selectedCategory?.name ?: "カテゴリなし",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("費目") },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = { selectedCategory = category; expanded = false },
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter(Char::isDigit) },
                    label = { Text("金額（円）") },
                )
                OutlinedTextField(value = memo, onValueChange = { memo = it }, label = { Text("メモ（任意）") })
            }
        },
        confirmButton = {
            Button(
                onClick = { selectedCategory?.let { onConfirm(it, amountText.toIntOrNull() ?: 0, memo) } },
                enabled = selectedCategory != null && (amountText.toIntOrNull() ?: 0) > 0,
            ) { Text("保存") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("キャンセル") } },
    )
}
