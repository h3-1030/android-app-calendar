package com.h31030.personalcalendar.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

enum class RepeatRule { NONE, WEEKLY, MONTHLY }

@Entity(tableName = "events", indices = [Index("date")])
data class EventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: LocalDate,
    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,
    val title: String,
    val memo: String = "",
    val repeatRule: RepeatRule = RepeatRule.NONE,
    val repeatUntil: LocalDate? = null,
)
