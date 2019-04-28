package workshop.akbolatss.xchangesrates.networking

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import workshop.akbolatss.xchangesrates.BuildConfig
import workshop.akbolatss.xchangesrates.model.ExchangeModel
import workshop.akbolatss.xchangesrates.networking.adapter.ExchangeTypeAdapter
import java.util.concurrent.TimeUnit

class RetrofitInstance {

    companion object {
        private var retrofitInstance: Retrofit? = null

        fun get(baseUrl: String): Retrofit {
            if (retrofitInstance == null)
                retrofitInstance = newInstance(baseUrl)
            return newInstance(baseUrl)
        }

        private fun newInstance(baseUrl: String): Retrofit {

            val gson = gsonInstance()

            val okHttpClient = okHttpClient()

            val retrofitBuilder = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okHttpClient)

            return retrofitBuilder.build()
        }

        public fun gsonInstance(): Gson {
            return GsonBuilder()
                    .registerTypeAdapter(ExchangeModel::class.java, ExchangeTypeAdapter())
                    .create()
        }

        private fun okHttpClient(): OkHttpClient {
            val okHttpBuilder = OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
            if (BuildConfig.DEBUG)
                okHttpBuilder.addInterceptor(loggingInterceptor())

            return okHttpBuilder.build()
        }

        private fun loggingInterceptor(): HttpLoggingInterceptor {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            return logging
        }

    }
}