package com.syzygyhub.base

import android.app.Application
import com.syzygyhub.base.di.AppModule

class SyzygyBaseApplication : Application() {

    lateinit var appModule: AppModule
        private set

    override fun onCreate() {
        super.onCreate()
        appModule = AppModule(this)
        appModule.setup()
    }
}
