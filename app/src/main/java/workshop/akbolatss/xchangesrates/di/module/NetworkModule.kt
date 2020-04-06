package workshop.akbolatss.xchangesrates.di.module

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import workshop.akbolatss.xchangesrates.BuildConfig
import workshop.akbolatss.xchangesrates.data.remote.adapter.ExchangeTypeAdapter
import workshop.akbolatss.xchangesrates.data.remote.service.ApiService
import workshop.akbolatss.xchangesrates.data.remote.model.ExchangeResponse
import workshop.akbolatss.xchangesrates.utils.Constants
import java.util.concurrent.TimeUnit

val apiModule = module {
    single { get<Retrofit>().create(ApiService::class.java) }
}

val networkModule = module {
    single {
        GsonBuilder().apply {
            registerTypeAdapter(ExchangeResponse::class.java, ExchangeTypeAdapter())
        }.create()
    }

    single {
        OkHttpClient.Builder().apply {
            connectTimeout(15, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
            if (BuildConfig.DEBUG)
                addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
        }.build()
    }

    single {
        Retrofit.Builder().apply {
            baseUrl(Constants.BASE_URL)
            addConverterFactory(GsonConverterFactory.create(get()))
            addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            client(get())
        }
    }
}
