package com.denchic45.binlist.di.module

import com.denchic45.binlist.ui.history.HistoryViewModel
import com.denchic45.binlist.ui.input.BinInputViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::BinInputViewModel)
    viewModelOf(::HistoryViewModel)
}