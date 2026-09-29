package com.h31030.personalcalendar.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.h31030.personalcalendar.data.local.dao.CategoryDao
import com.h31030.personalcalendar.data.local.dao.DiaryDao
import com.h31030.personalcalendar.data.local.dao.EventDao
import com.h31030.personalcalendar.data.local.dao.TodoDao
import com.h31030.personalcalendar.data.local.dao.TransactionDao
import com.h31030.personalcalendar.data.local.entity.CategoryEntity
import com.h31030.personalcalendar.data.local.entity.DiaryEntryEntity
import com.h31030.personalcalendar.data.local.entity.EventEntity
import com.h31030.personalcalendar.data.local.entity.TodoEntity
import com.h31030.personalcalendar.data.local.entity.TransactionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [
        EventEntity::class,
        TodoEntity::class,
        DiaryEntryEntity::class,
        TransactionEntity::class,
        CategoryEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun todoDao(): TodoDao
    abstract fun diaryDao(): DiaryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        private const val DATABASE_NAME = "personal_calendar.db"

        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context, applicationScope: CoroutineScope): AppDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DATABASE_NAME)
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            instance?.let { database ->
                                applicationScope.launch {
                                    database.categoryDao().seedIfEmpty()
                                }
                            }
                        }
                    })
                    .build()
                    .also { instance = it }
            }
        }
    }
}
