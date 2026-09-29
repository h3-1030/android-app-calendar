package com.h31030.personalcalendar.ui.daydetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.h31030.personalcalendar.data.local.entity.CategoryEntity
import com.h31030.personalcalendar.data.local.entity.DiaryEntryEntity
import com.h31030.personalcalendar.data.local.entity.EventEntity
import com.h31030.personalcalendar.data.local.entity.RepeatRule
import com.h31030.personalcalendar.data.local.entity.TodoEntity
import com.h31030.personalcalendar.data.local.entity.TransactionEntity
import com.h31030.personalcalendar.data.repository.CategoryRepository
import com.h31030.personalcalendar.data.repository.DiaryRepository
import com.h31030.personalcalendar.data.repository.EventRepository
import com.h31030.personalcalendar.data.repository.TodoRepository
import com.h31030.personalcalendar.data.repository.TransactionRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DayDetailUiState(
    val date: LocalDate,
    val events: List<EventEntity> = emptyList(),
    val todos: List<TodoEntity> = emptyList(),
    val diary: DiaryEntryEntity? = null,
    val transactions: List<TransactionEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
)

class DayDetailViewModel(
    private val date: LocalDate,
    private val eventRepository: EventRepository,
    private val todoRepository: TodoRepository,
    private val diaryRepository: DiaryRepository,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    val uiState: StateFlow<DayDetailUiState> = combine(
        eventRepository.observeByDate(date),
        todoRepository.observeByDate(date),
        diaryRepository.observeByDate(date),
        transactionRepository.observeByDate(date),
        categoryRepository.observeAll(),
    ) { events, todos, diary, transactions, categories ->
        DayDetailUiState(
            date = date,
            events = events,
            todos = todos,
            diary = diary,
            transactions = transactions,
            categories = categories,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DayDetailUiState(date = date))

    fun addEvent(title: String, memo: String, repeatRule: RepeatRule = RepeatRule.NONE) {
        if (title.isBlank()) return
        viewModelScope.launch {
            eventRepository.save(
                EventEntity(date = date, title = title.trim(), memo = memo.trim(), repeatRule = repeatRule),
            )
        }
    }

    fun deleteEvent(event: EventEntity) {
        viewModelScope.launch { eventRepository.delete(event) }
    }

    fun addTodo(title: String, dueDate: LocalDate?) {
        if (title.isBlank()) return
        viewModelScope.launch {
            todoRepository.save(TodoEntity(title = title.trim(), dueDate = dueDate))
        }
    }

    fun toggleTodoDone(todo: TodoEntity) {
        viewModelScope.launch { todoRepository.setDone(todo, !todo.isDone) }
    }

    fun deleteTodo(todo: TodoEntity) {
        viewModelScope.launch { todoRepository.delete(todo) }
    }

    fun saveDiary(content: String) {
        viewModelScope.launch {
            if (content.isBlank()) {
                uiState.value.diary?.let { diaryRepository.delete(it) }
            } else {
                diaryRepository.save(DiaryEntryEntity(date = date, content = content.trim()))
            }
        }
    }

    fun addTransaction(category: CategoryEntity, amount: Int, memo: String) {
        if (amount <= 0) return
        viewModelScope.launch {
            transactionRepository.save(
                TransactionEntity(
                    date = date,
                    type = category.type,
                    amount = amount,
                    categoryId = category.id,
                    memo = memo.trim(),
                ),
            )
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch { transactionRepository.delete(transaction) }
    }

    companion object {
        fun factory(
            date: LocalDate,
            eventRepository: EventRepository,
            todoRepository: TodoRepository,
            diaryRepository: DiaryRepository,
            transactionRepository: TransactionRepository,
            categoryRepository: CategoryRepository,
        ) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                DayDetailViewModel(
                    date,
                    eventRepository,
                    todoRepository,
                    diaryRepository,
                    transactionRepository,
                    categoryRepository,
                ) as T
        }
    }
}
