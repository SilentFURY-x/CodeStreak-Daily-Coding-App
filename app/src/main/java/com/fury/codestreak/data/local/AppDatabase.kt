package com.fury.codestreak.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [QuestionEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract val questionDao: QuestionDao
}