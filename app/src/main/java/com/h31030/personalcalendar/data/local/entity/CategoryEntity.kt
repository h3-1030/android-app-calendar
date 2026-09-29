package com.h31030.personalcalendar.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType { INCOME, EXPENSE }

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: TransactionType,
    /** クイック入力ボタンとして表示するかどうか（設定画面から切り替え可能）。 */
    val isQuickAccess: Boolean = true,
    val sortOrder: Int = 0,
)
