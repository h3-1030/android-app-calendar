package com.h31030.personalcalendar.data.repository

import com.h31030.personalcalendar.data.local.dao.TodoDao
import com.h31030.personalcalendar.data.local.entity.TodoEntity
import java.time.LocalDate
import java.time.LocalDateTime
import kotlinx.coroutines.flow.Flow

class TodoRepository(private val todoDao: TodoDao) {
    fun observeAll(): Flow<List<TodoEntity>> = todoDao.observeAll()

    fun observeByDate(date: LocalDate): Flow<List<TodoEntity>> = todoDao.observeByDate(date)

    fun observeDueDatesInRange(start: LocalDate, end: LocalDate): Flow<List<LocalDate>> =
        todoDao.observeDueDatesInRange(start, end)

    suspend fun save(todo: TodoEntity) {
        if (todo.id == 0L) todoDao.insert(todo) else todoDao.update(todo)
    }

    suspend fun setDone(todo: TodoEntity, isDone: Boolean) {
        todoDao.update(todo.copy(isDone = isDone, completedAt = if (isDone) LocalDateTime.now() else null))
    }

    suspend fun delete(todo: TodoEntity) = todoDao.delete(todo)
}
