package workshop.akbolatss.xchangesrates.app

import android.app.Application
import android.util.ArrayMap

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.orhanobut.hawk.Hawk

import org.greenrobot.greendao.database.Database
import org.json.JSONException
import org.json.JSONObject

import java.lang.reflect.Type
import java.util.ArrayList
import java.util.concurrent.TimeUnit

import me.yokeyword.fragmentation.BuildConfig
import me.yokeyword.fragmentation.Fragmentation
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import workshop.akbolatss.xchangesrates.model.ExchangeModel
import workshop.akbolatss.xchangesrates.model.dao.DaoMaster
import workshop.akbolatss.xchangesrates.model.dao.DaoSession
import workshop.akbolatss.xchangesrates.networking.APIService
import workshop.akbolatss.xchangesrates.repositories.DBOpenHelper

import workshop.akbolatss.xchangesrates.utils.Constants.BASE_URL
import workshop.akbolatss.xchangesrates.utils.Constants.DB_SNAPS_NAME

class ApplicationMain : Application() {

    private lateinit var mRetrofit: Retrofit

    lateinit var daoSession: DaoSession

    override fun onCreate() {
        super.onCreate()

        Hawk.init(applicationContext).build()

        daoSession = provideDaoSession()

        // Gson кастомизация
        val gson = GsonBuilder()
                .registerTypeAdapter(ExchangeModel::class.java, JsonDeserializer<ExchangeModel> { json, typeOfT, context ->
                    var exchangeModel: ExchangeModel? = null
                    var jsonObject: JSONObject? = null
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

        //        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        //        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        val client = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                //                .addInterceptor(logging)
                .build()

        mRetrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()

        Fragmentation.builder()
                .stackViewMode(Fragmentation.BUBBLE)
                .debug(BuildConfig.DEBUG)
                .install()

        apiService = mRetrofit.create(APIService::class.java)
    }

    private fun provideDaoSession(): DaoSession {
        val helper = DBOpenHelper(this, DB_SNAPS_NAME)
        val db = if (ENCRYPTED) helper.getEncryptedWritableDb("super-secret") else helper.writableDb
        return DaoMaster(db).newSession()
    }

    companion object {

        private const val ENCRYPTED = false

        lateinit var apiService: APIService
    }
}
