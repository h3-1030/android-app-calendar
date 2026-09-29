package com.h31030.personalcalendar.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "todos", indices = [Index("dueDate")])
data class TodoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val memo: String = "",
    val dueDate: LocalDate? = null,
    val isDone: Boolean = false,
    val completedAt: LocalDateTime? = null,
)
