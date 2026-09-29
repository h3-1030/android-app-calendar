package com.h31030.personalcalendar.data.local

import com.h31030.personalcalendar.data.local.dao.CategoryDao
import com.h31030.personalcalendar.data.local.entity.CategoryEntity
import com.h31030.personalcalendar.data.local.entity.TransactionType

/**
 * 家計簿の初期カテゴリ（要件定義書 4.4 節）。
 * 初回起動時のみ [seedIfEmpty] でシードする。
 */
private val defaultExpenseCategories = listOf("外食", "アミューズメント", "カフェ", "コーヒー豆", "食材")
private val defaultIncomeCategories = listOf("給与")

suspend fun CategoryDao.seedIfEmpty() {
    if (count() > 0) return

    val seed = buildList {
        defaultExpenseCategories.forEachIndexed { index, name ->
            add(CategoryEntity(name = name, type = TransactionType.EXPENSE, sortOrder = index))
        }
        defaultIncomeCategories.forEachIndexed { index, name ->
            add(CategoryEntity(name = name, type = TransactionType.INCOME, sortOrder = index))
        }
    }
    insertAll(seed)
}
