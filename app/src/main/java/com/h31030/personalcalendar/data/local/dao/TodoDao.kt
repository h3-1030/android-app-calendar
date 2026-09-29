package com.h31030.personalcalendar.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.h31030.personalcalendar.data.local.entity.TodoEntity
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos ORDER BY isDone ASC, dueDate IS NULL, dueDate ASC, id DESC")
    fun observeAll(): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE dueDate = :date ORDER BY isDone ASC, id DESC")
    fun observeByDate(date: LocalDate): Flow<List<TodoEntity>>

    @Query("SELECT DISTINCT dueDate FROM todos WHERE dueDate BETWEEN :start AND :end")
    fun observeDueDatesInRange(start: LocalDate, end: LocalDate): Flow<List<LocalDate>>

    @Insert
    suspend fun insert(todo: TodoEntity): Long

    @Update
    suspend fun update(todo: TodoEntity)

    @Delete
    suspend fun delete(todo: TodoEntity)
}
