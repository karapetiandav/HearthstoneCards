package ru.karapetiandav.hearthstonecards

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import ru.karapetiandav.hearthstonecards.di.modules.Modules
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(listOf(Modules.appModule, Modules.mainModule, Modules.cardsModule, Modules.authModule))
        }
    }
}