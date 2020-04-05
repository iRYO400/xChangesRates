package workshop.akbolatss.xchangesrates.app

import android.app.Application
import android.content.Context
import com.orhanobut.hawk.Hawk
import me.yokeyword.fragmentation.BuildConfig
import me.yokeyword.fragmentation.Fragmentation
import retrofit2.Retrofit
import workshop.akbolatss.xchangesrates.data.persistent.AppDataBase
import workshop.akbolatss.xchangesrates.data.persistent.RoomInstance
import workshop.akbolatss.xchangesrates.data.remote.RetrofitInstance
import workshop.akbolatss.xchangesrates.di.KoinInjector
import workshop.akbolatss.xchangesrates.utils.Constants
import workshop.akbolatss.xchangesrates.utils.Constants.BASE_URL
import workshop.akbolatss.xchangesrates.utils.UtilityMethods
import workshop.akbolatss.xchangesrates.utils.logging.TimberLogImplementation


class ApplicationMain : Application() {

    companion object {

        lateinit var instance: ApplicationMain

        fun getRetrofit(): Retrofit {
            return RetrofitInstance.get(BASE_URL)
        }

        fun getRoom(context: Context): AppDataBase {
            return RoomInstance.get(context)
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        TimberLogImplementation.init()
        initKoin()
        initHawk()
        initFragmentation()

        initNotificationChannel()
    }

    private fun initKoin() {
        KoinInjector.init(this)
    }

    private fun initHawk() {
        Hawk.init(applicationContext).build()
    }

    private fun initFragmentation() {
        Fragmentation.builder()
                .stackViewMode(Fragmentation.BUBBLE)
                .debug(BuildConfig.DEBUG)
                .install()
    }

    private fun initNotificationChannel() {
        if (!Hawk.contains(Constants.HAWK_CHANNEL_CREATED)) {
            Hawk.put(Constants.HAWK_CHANNEL_CREATED, true)
            UtilityMethods.createDefaultNotificationChannel(this)
        }
    }
}
