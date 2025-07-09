package com.denchic45.binlist.di.module

import com.denchic45.binlist.domain.usecase.FindBinDetailsUseCase
import com.denchic45.binlist.domain.usecase.FindSaveBinDetailsUseCase
import org.koin.dsl.module

val domainModule = module {
    includes(dataModule)
    single { FindBinDetailsUseCase(get()) }
    single { FindSaveBinDetailsUseCase(get()) }
}