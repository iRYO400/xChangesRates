package workshop.akbolatss.xchangesrates.app

import android.app.Application
import android.arch.persistence.room.Room
import android.util.ArrayMap
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.orhanobut.hawk.Hawk
import me.yokeyword.fragmentation.BuildConfig
import me.yokeyword.fragmentation.Fragmentation
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import workshop.akbolatss.xchangesrates.model.ExchangeModel
import workshop.akbolatss.xchangesrates.networking.APIService
import workshop.akbolatss.xchangesrates.networking.adapter.ExchangeTypeAdapter
import workshop.akbolatss.xchangesrates.room.AppDataBase
import workshop.akbolatss.xchangesrates.utils.Constants
import workshop.akbolatss.xchangesrates.utils.Constants.BASE_URL
import java.util.*
import java.util.concurrent.TimeUnit


class ApplicationMain : Application() {

    private lateinit var mRetrofit: Retrofit

    lateinit var appDatabase: AppDataBase

    lateinit var mGson: Gson

    companion object {


        lateinit var instance: ApplicationMain

        lateinit var apiService: APIService
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Hawk.init(applicationContext).build()

        mGson = buildGson()
        appDatabase = Room.databaseBuilder(applicationContext, AppDataBase::class.java, Constants.DB_SNAPS_NAME_NEW)
                .build()

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY;

        val client = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()

        mRetrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(mGson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()

        Fragmentation.builder()
                .stackViewMode(Fragmentation.BUBBLE)
                .debug(BuildConfig.DEBUG)
                .install()

        apiService = mRetrofit.create(APIService::class.java)

    }

    private fun buildGson(): Gson {
        return GsonBuilder()
                .registerTypeAdapter(ExchangeModel::class.java, ExchangeTypeAdapter())
                .create()
    }
}
