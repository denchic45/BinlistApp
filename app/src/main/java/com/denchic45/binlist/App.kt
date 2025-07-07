package com.denchic45.binlist

import android.app.Application
import com.denchic45.binlist.di.module.apiModule
import com.denchic45.binlist.di.module.dataModule
import com.denchic45.binlist.di.module.domainModule
import com.denchic45.binlist.di.module.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(apiModule, dataModule, domainModule, viewModelModule)
        }
    }
}