package com.h31030.personalcalendar.ui.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.h31030.personalcalendar.R
import com.h31030.personalcalendar.data.local.entity.TransactionType
import com.h31030.personalcalendar.ui.LocalAppContainer
import com.h31030.personalcalendar.ui.theme.IndicatorExpense
import com.h31030.personalcalendar.ui.theme.IndicatorIncome

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetSummaryScreen() {
    val container = LocalAppContainer.current
    val viewModel: BudgetSummaryViewModel = viewModel(
        factory = BudgetSummaryViewModel.factory(container.transactionRepository, container.categoryRepository),
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val net = uiState.totalIncome - uiState.totalExpense
    val maxCategoryTotal = uiState.categoryTotals.maxOfOrNull { it.total } ?: 1

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.nav_budget)) }) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = viewModel::goToPreviousMonth) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "前の月")
                    }
                    Text(
                        text = "${uiState.month.year}年${uiState.month.monthValue}月",
                        style = MaterialTheme.typography.titleLarge,
                    )
                    IconButton(onClick = viewModel::goToNextMonth) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "次の月")
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SummaryRow(label = "収入", amount = uiState.totalIncome, color = IndicatorIncome)
                        SummaryRow(label = "支出", amount = uiState.totalExpense, color = IndicatorExpense)
                        SummaryRow(label = "収支", amount = net, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            item {
                Text(text = "カテゴリ別内訳", style = MaterialTheme.typography.titleMedium)
            }

            items(uiState.categoryTotals) { categoryTotal ->
                CategoryBarRow(
                    name = categoryTotal.categoryName,
                    amount = categoryTotal.total,
                    fraction = categoryTotal.total.toFloat() / maxCategoryTotal.toFloat(),
                    color = if (categoryTotal.type == TransactionType.EXPENSE) IndicatorExpense else IndicatorIncome,
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, amount: Int, color: androidx.compose.ui.graphics.Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(text = "${amount}円", style = MaterialTheme.typography.bodyLarge, color = color)
    }
}

@Composable
private fun CategoryBarRow(name: String, amount: Int, fraction: Float, color: androidx.compose.ui.graphics.Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = name, style = MaterialTheme.typography.bodyMedium)
            Text(text = "${amount}円", style = MaterialTheme.typography.bodyMedium)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction.coerceIn(0f, 1f))
                    .height(8.dp)
                    .background(color, RoundedCornerShape(4.dp)),
            )
        }
    }
}
