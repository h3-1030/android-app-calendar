package com.h31030.personalcalendar

import android.content.Context
import com.h31030.personalcalendar.data.local.AppDatabase
import com.h31030.personalcalendar.data.repository.CategoryRepository
import com.h31030.personalcalendar.data.repository.DiaryRepository
import com.h31030.personalcalendar.data.repository.EventRepository
import com.h31030.personalcalendar.data.repository.TodoRepository
import com.h31030.personalcalendar.data.repository.TransactionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

/**
 * 手動DIコンテナ。Hilt等のDIフレームワークは使わず、
 * [PersonalCalendarApplication] が保持する単純なオブジェクトグラフとして提供する。
 */
class AppContainer(context: Context) {
    private val applicationScope = CoroutineScope(SupervisorJob())

    private val database: AppDatabase = AppDatabase.getDatabase(context, applicationScope)

    val eventRepository: EventRepository by lazy { EventRepository(database.eventDao()) }
    val todoRepository: TodoRepository by lazy { TodoRepository(database.todoDao()) }
    val diaryRepository: DiaryRepository by lazy { DiaryRepository(database.diaryDao()) }
    val transactionRepository: TransactionRepository by lazy { TransactionRepository(database.transactionDao()) }
    val categoryRepository: CategoryRepository by lazy { CategoryRepository(database.categoryDao()) }
}
