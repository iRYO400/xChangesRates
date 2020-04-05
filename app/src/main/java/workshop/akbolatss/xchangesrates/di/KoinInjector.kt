package workshop.akbolatss.xchangesrates.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin

object KoinInjector {
    fun init(application: Application) {
        startKoin {
            androidContext(application)
            loadKoinModules(listOf(
                    viewModelModule
            ))
        }
    }
}
