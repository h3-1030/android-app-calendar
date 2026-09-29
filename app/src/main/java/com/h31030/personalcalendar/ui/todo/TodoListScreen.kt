package com.h31030.personalcalendar.ui.todo

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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.h31030.personalcalendar.R
import com.h31030.personalcalendar.data.local.entity.TodoEntity
import com.h31030.personalcalendar.ui.LocalAppContainer
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen() {
    val container = LocalAppContainer.current
    val viewModel: TodoListViewModel = viewModel(factory = TodoListViewModel.factory(container.todoRepository))
    val todos by viewModel.todos.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.nav_todo)) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.action_add))
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
        ) {
            items(todos, key = { it.id }) { todo ->
                TodoItemRow(
                    todo = todo,
                    onToggle = { viewModel.toggleDone(todo) },
                    onDelete = { viewModel.delete(todo) },
                )
            }
        }
    }

    if (showAddDialog) {
        AddTodoDialog(
            onConfirm = { title -> viewModel.addTodo(title); showAddDialog = false },
            onDismiss = { showAddDialog = false },
        )
    }
}

@Composable
private fun TodoItemRow(todo: TodoEntity, onToggle: () -> Unit, onDelete: () -> Unit) {
    val today = java.time.LocalDate.now()
    val isOverdue = todo.dueDate != null && !todo.isDone && todo.dueDate.isBefore(today)

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
        ) {
            Row {
                Checkbox(checked = todo.isDone, onCheckedChange = { onToggle() })
                androidx.compose.foundation.layout.Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(
                        text = todo.title,
                        textDecoration = if (todo.isDone) TextDecoration.LineThrough else null,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    todo.dueDate?.let { due ->
                        Text(
                            text = due.format(DateTimeFormatter.ofPattern("M月d日")),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.action_delete))
            }
        }
    }
}

@Composable
private fun AddTodoDialog(onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var title by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Todoを追加") },
        text = {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("タイトル") })
        },
        confirmButton = {
            Button(onClick = { onConfirm(title) }, enabled = title.isNotBlank()) { Text(stringResource(R.string.action_save)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) } },
    )
}
