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

        private const val ENCRYPTED = false

        lateinit var instance: ApplicationMain

        lateinit var apiService: APIService
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Hawk.init(applicationContext).build()

        mGson = buildGson()
        appDatabase = Room.databaseBuilder(applicationContext, AppDataBase::class.java, Constants.DB_SNAPS_NAME_NEW)
//                .addMigrations(MIGRATION_1_2)
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
                .registerTypeAdapter(ExchangeModel::class.java, JsonDeserializer<ExchangeModel> { json, _, _ ->
                    var exchangeModel: ExchangeModel? = null
                    val jsonObject: JSONObject?
                    try {
                        jsonObject = JSONObject(json.toString())
                        val iterator = jsonObject.keys()
                        iterator.next() // skip id
                        iterator.next() // skip caption

                        val listMap = ArrayMap<String, List<String>>()
                        var buffCurrency: MutableList<String>? = null
                        var buff = ""
                        while (iterator.hasNext()) {
                            buff = iterator.next().toString()

                            buffCurrency = ArrayList()
                            for (i in 0 until jsonObject.getJSONArray(buff).length()) {
                                buffCurrency.add(jsonObject.getJSONArray(buff).getString(i))
                            }
                            listMap[buff] = buffCurrency
                        }

                        exchangeModel = ExchangeModel(
                                jsonObject.getString("ident"),
                                jsonObject.getString("caption"),
                                listMap)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    exchangeModel
                })
                .create()
    }
}
