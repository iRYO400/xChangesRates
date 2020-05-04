@file:Suppress("unused")

package workshop.akbolatss.xchangesrates.app

import android.app.Application
import com.orhanobut.hawk.Hawk
import me.yokeyword.fragmentation.BuildConfig
import me.yokeyword.fragmentation.Fragmentation
import timber.log.Timber
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.di.KoinInjector
import workshop.akbolatss.xchangesrates.utils.logging.TimberLogImplementation

class ApplicationMain : Application() {

    override fun onCreate() {
        super.onCreate()
        initHawk()
        TimberLogImplementation.init()
        initKoin()
        initFragmentation()
    }

    private fun initHawk() {
        Hawk.init(applicationContext).build()
    }

    private fun initKoin() {
        KoinInjector.init(this)
    }

    private fun initFragmentation() {
        Fragmentation.builder()
            .stackViewMode(Fragmentation.BUBBLE)
            .debug(BuildConfig.DEBUG)
            .install()
    }

}
