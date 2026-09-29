package com.h31030.personalcalendar.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

/** 1日1件（date にユニーク制約）を想定した軽量な日記エントリー。 */
@Entity(tableName = "diary_entries", indices = [Index("date", unique = true)])
data class DiaryEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: LocalDate,
    val content: String,
)
