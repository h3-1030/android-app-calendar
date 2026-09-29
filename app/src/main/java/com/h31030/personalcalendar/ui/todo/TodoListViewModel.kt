package com.h31030.personalcalendar.ui.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.h31030.personalcalendar.data.local.entity.TodoEntity
import com.h31030.personalcalendar.data.repository.TodoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoListViewModel(private val todoRepository: TodoRepository) : ViewModel() {

    val todos: StateFlow<List<TodoEntity>> = todoRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun toggleDone(todo: TodoEntity) {
        viewModelScope.launch { todoRepository.setDone(todo, !todo.isDone) }
    }

    fun delete(todo: TodoEntity) {
        viewModelScope.launch { todoRepository.delete(todo) }
    }

    fun addTodo(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch { todoRepository.save(TodoEntity(title = title.trim())) }
    }

    companion object {
        fun factory(todoRepository: TodoRepository) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                TodoListViewModel(todoRepository) as T
        }
    }
}
