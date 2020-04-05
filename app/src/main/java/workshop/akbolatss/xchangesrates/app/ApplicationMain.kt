package workshop.akbolatss.xchangesrates.app

import android.app.Application
import android.content.Context
import com.orhanobut.hawk.Hawk
import me.yokeyword.fragmentation.BuildConfig
import me.yokeyword.fragmentation.Fragmentation
import retrofit2.Retrofit
import workshop.akbolatss.xchangesrates.networking.RetrofitInstance
import workshop.akbolatss.xchangesrates.room.AppDataBase
import workshop.akbolatss.xchangesrates.room.RoomInstance
import workshop.akbolatss.xchangesrates.utils.Constants.BASE_URL


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
        initHawk()
        initFragmentation()
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
}
