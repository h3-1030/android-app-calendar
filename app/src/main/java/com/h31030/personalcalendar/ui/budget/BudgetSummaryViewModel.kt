package com.h31030.personalcalendar.ui.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.h31030.personalcalendar.data.local.entity.TransactionType
import com.h31030.personalcalendar.data.repository.CategoryRepository
import com.h31030.personalcalendar.data.repository.TransactionRepository
import java.time.YearMonth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

data class CategoryTotal(val categoryName: String, val type: TransactionType, val total: Int)

data class BudgetSummaryUiState(
    val month: YearMonth = YearMonth.now(),
    val totalIncome: Int = 0,
    val totalExpense: Int = 0,
    val categoryTotals: List<CategoryTotal> = emptyList(),
)

@OptIn(ExperimentalCoroutinesApi::class)
class BudgetSummaryViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val month = MutableStateFlow(YearMonth.now())

    val uiState: StateFlow<BudgetSummaryUiState> = month
        .flatMapLatest { yearMonth ->
            val start = yearMonth.atDay(1)
            val end = yearMonth.atEndOfMonth()
            combine(
                transactionRepository.observeByDateRange(start, end),
                categoryRepository.observeAll(),
            ) { transactions, categories ->
                val categoryNames = categories.associateBy { it.id }
                val totalsByCategory = transactions
                    .groupBy { it.categoryId }
                    .mapNotNull { (categoryId, items) ->
                        val category = categoryNames[categoryId] ?: return@mapNotNull null
                        CategoryTotal(
                            categoryName = category.name,
                            type = category.type,
                            total = items.sumOf { it.amount },
                        )
                    }
                    .sortedByDescending { it.total }

                BudgetSummaryUiState(
                    month = yearMonth,
                    totalIncome = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount },
                    totalExpense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount },
                    categoryTotals = totalsByCategory,
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BudgetSummaryUiState())

    fun goToPreviousMonth() {
        month.value = month.value.minusMonths(1)
    }

    fun goToNextMonth() {
        month.value = month.value.plusMonths(1)
    }

    companion object {
        fun factory(transactionRepository: TransactionRepository, categoryRepository: CategoryRepository) =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    BudgetSummaryViewModel(transactionRepository, categoryRepository) as T
            }
    }
}
