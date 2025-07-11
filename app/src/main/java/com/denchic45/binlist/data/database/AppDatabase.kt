package com.denchic45.binlist.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 1,
    entities = [BinDetailsEntity::class]
)
abstract class AppDatabase : RoomDatabase() {
    abstract val binDetailsDao: BinDetailsDao

}