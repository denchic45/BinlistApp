package com.denchic45.binlist.di.module

import android.content.Context
import androidx.room.Room
import com.denchic45.binlist.data.database.AppDatabase
import org.koin.dsl.module


val databaseModule = module {
    single {
        Room.databaseBuilder(
            get<Context>().applicationContext,
            AppDatabase::class.java,
            "users_db"
        ).fallbackToDestructiveMigration(false)
            .build()
    }
    single { get<AppDatabase>().binDetailsDao }
}