package com.h31030.personalcalendar.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.h31030.personalcalendar.data.local.entity.CategoryEntity
import com.h31030.personalcalendar.data.local.entity.DiaryEntryEntity
import com.h31030.personalcalendar.data.local.entity.EventEntity
import com.h31030.personalcalendar.data.local.entity.TodoEntity
import com.h31030.personalcalendar.data.local.entity.TransactionEntity
import com.h31030.personalcalendar.data.local.entity.TransactionType
import com.h31030.personalcalendar.data.repository.CategoryRepository
import com.h31030.personalcalendar.data.repository.DiaryRepository
import com.h31030.personalcalendar.data.repository.EventRepository
import com.h31030.personalcalendar.data.repository.TodoRepository
import com.h31030.personalcalendar.data.repository.TransactionRepository
import com.h31030.personalcalendar.ui.model.DayIndicator
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CalendarUiState(
    val month: YearMonth = YearMonth.now(),
    val today: LocalDate = LocalDate.now(),
    val indicatorsByDate: Map<LocalDate, Set<DayIndicator>> = emptyMap(),
    val quickAccessCategories: List<CategoryEntity> = emptyList(),
)

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModel(
    private val eventRepository: EventRepository,
    private val todoRepository: TodoRepository,
    private val diaryRepository: DiaryRepository,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val month = MutableStateFlow(YearMonth.now())

    val uiState: StateFlow<CalendarUiState> = month
        .flatMapLatest { yearMonth ->
            val start = yearMonth.atDay(1)
            val end = yearMonth.atEndOfMonth()
            combine(
                eventRepository.observeDatesInRange(start, end),
                todoRepository.observeDueDatesInRange(start, end),
                diaryRepository.observeDatesInRange(start, end),
                transactionRepository.observeByDateRange(start, end),
                categoryRepository.observeQuickAccess(),
            ) { eventDates, todoDates, diaryDates, transactions, quickCategories ->
                val indicators = mutableMapOf<LocalDate, MutableSet<DayIndicator>>()
                fun mark(date: LocalDate, indicator: DayIndicator) {
                    indicators.getOrPut(date) { mutableSetOf() }.add(indicator)
                }
                eventDates.forEach { mark(it, DayIndicator.EVENT) }
                todoDates.forEach { mark(it, DayIndicator.TODO) }
                diaryDates.forEach { mark(it, DayIndicator.DIARY) }
                transactions.forEach {
                    mark(it.date, if (it.type == TransactionType.EXPENSE) DayIndicator.EXPENSE else DayIndicator.INCOME)
                }
                CalendarUiState(
                    month = yearMonth,
                    indicatorsByDate = indicators,
                    quickAccessCategories = quickCategories,
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CalendarUiState())

    fun goToPreviousMonth() {
        month.value = month.value.minusMonths(1)
    }

    fun goToNextMonth() {
        month.value = month.value.plusMonths(1)
    }

    fun goToToday() {
        month.value = YearMonth.now()
    }

    /** クイック追加「予定」：タイトルのみで当日に登録する。 */
    fun quickAddEvent(title: String, date: LocalDate = LocalDate.now()) {
        if (title.isBlank()) return
        viewModelScope.launch {
            eventRepository.save(EventEntity(date = date, title = title.trim()))
        }
    }

    /** クイック追加「todo」：タイトルのみで期日なしで登録する。 */
    fun quickAddTodo(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            todoRepository.save(TodoEntity(title = title.trim()))
        }
    }

    /** クイック追加「日記」：当日分に上書き保存する。 */
    fun quickSaveDiary(content: String, date: LocalDate = LocalDate.now()) {
        if (content.isBlank()) return
        viewModelScope.launch {
            diaryRepository.save(DiaryEntryEntity(date = date, content = content.trim()))
        }
    }

    /** クイック追加「家計簿」：費目ボタン経由で金額のみ入力して保存する。 */
    fun quickAddTransaction(category: CategoryEntity, amount: Int, date: LocalDate = LocalDate.now()) {
        if (amount <= 0) return
        viewModelScope.launch {
            transactionRepository.save(
                TransactionEntity(date = date, type = category.type, amount = amount, categoryId = category.id),
            )
        }
    }

    companion object {
        fun factory(
            eventRepository: EventRepository,
            todoRepository: TodoRepository,
            diaryRepository: DiaryRepository,
            transactionRepository: TransactionRepository,
            categoryRepository: CategoryRepository,
        ) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                CalendarViewModel(
                    eventRepository,
                    todoRepository,
                    diaryRepository,
                    transactionRepository,
                    categoryRepository,
                ) as T
        }
    }
}
