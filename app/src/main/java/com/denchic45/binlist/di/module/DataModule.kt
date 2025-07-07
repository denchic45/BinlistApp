package com.denchic45.binlist.di.module

import com.denchic45.binlist.data.repository.BinRepository
import org.koin.dsl.module

val dataModule = module {
    includes(apiModule, databaseModule)
    single { BinRepository(get(), get()) }
}