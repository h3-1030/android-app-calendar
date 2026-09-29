package com.h31030.personalcalendar.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.h31030.personalcalendar.ui.LocalAppContainer
import com.h31030.personalcalendar.ui.model.DayIndicator
import com.h31030.personalcalendar.ui.quickadd.QuickAddSheet
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

private val weekdayOrder = listOf(
    DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
    DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(onDateClick: (LocalDate) -> Unit) {
    val container = LocalAppContainer.current
    val viewModel: CalendarViewModel = viewModel(
        factory = CalendarViewModel.factory(
            container.eventRepository,
            container.todoRepository,
            container.diaryRepository,
            container.transactionRepository,
            container.categoryRepository,
        ),
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showQuickAdd by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showQuickAdd = true }) {
                Icon(Icons.Default.Add, contentDescription = "クイック追加")
            }
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            MonthHeader(
                month = uiState.month,
                onPrevious = viewModel::goToPreviousMonth,
                onNext = viewModel::goToNextMonth,
                onToday = viewModel::goToToday,
            )
            WeekdayRow()
            MonthGrid(
                month = uiState.month,
                today = uiState.today,
                indicatorsByDate = uiState.indicatorsByDate,
                onDateClick = onDateClick,
            )
        }
    }

    if (showQuickAdd) {
        QuickAddSheet(
            quickAccessCategories = uiState.quickAccessCategories,
            onDismiss = { showQuickAdd = false },
            onAddEvent = { title -> viewModel.quickAddEvent(title) },
            onAddTodo = { title -> viewModel.quickAddTodo(title) },
            onSaveDiary = { content -> viewModel.quickSaveDiary(content) },
            onAddTransaction = { category, amount -> viewModel.quickAddTransaction(category, amount) },
        )
    }
}

@Composable
private fun MonthHeader(month: YearMonth, onPrevious: () -> Unit, onNext: () -> Unit, onToday: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "前の月")
        }
        Text(
            text = "${month.year}年${month.monthValue}月",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.clickable { onToday() },
        )
        IconButton(onClick = onNext) {
            Icon(Icons.Default.ChevronRight, contentDescription = "次の月")
        }
    }
}

@Composable
private fun WeekdayRow() {
    Row(modifier = Modifier.fillMaxWidth()) {
        weekdayOrder.forEach { day ->
            Text(
                text = day.getDisplayName(TextStyle.SHORT, Locale.JAPAN),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Composable
private fun MonthGrid(
    month: YearMonth,
    today: LocalDate,
    indicatorsByDate: Map<LocalDate, Set<DayIndicator>>,
    onDateClick: (LocalDate) -> Unit,
) {
    val firstDay = month.atDay(1)
    // 日曜始まりなので、月初のSUNDAYからのオフセットを求める（SUNDAY=7 として扱う）
    val leadingBlanks = firstDay.dayOfWeek.value % 7
    val daysInMonth = month.lengthOfMonth()
    val cells = buildList {
        repeat(leadingBlanks) { add(null) }
        for (day in 1..daysInMonth) add(month.atDay(day))
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.padding(4.dp),
    ) {
        items(cells) { date ->
            if (date == null) {
                Box(modifier = Modifier.aspectRatio(1f))
            } else {
                DayCell(
                    date = date,
                    isToday = date == today,
                    indicators = indicatorsByDate[date].orEmpty(),
                    onClick = { onDateClick(date) },
                )
            }
        }
    }
}

@Composable
private fun DayCell(date: LocalDate, isToday: Boolean, indicators: Set<DayIndicator>, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .then(
                    if (isToday) {
                        Modifier.background(MaterialTheme.colorScheme.primary, CircleShape)
                    } else {
                        Modifier
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                color = if (isToday) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            indicators.take(4).forEach { indicator ->
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .background(indicator.color, CircleShape),
                )
            }
        }
    }
}
