package com.devts.mymeal

import android.app.Application
import com.devts.mymeal.di.initKoin
import org.koin.android.ext.koin.androidContext

class MymealApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin { androidContext(this@MymealApplication) }
    }
}
