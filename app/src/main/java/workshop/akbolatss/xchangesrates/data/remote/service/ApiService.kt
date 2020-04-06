package workshop.akbolatss.xchangesrates.data.remote.service

import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import workshop.akbolatss.xchangesrates.data.remote.model.ChartResponse
import workshop.akbolatss.xchangesrates.data.remote.model.StatsResponse

/**
 * Author: Akbolat Sadvakassov
 * Date: 20.11.2017
 */
//"The Term field needs to contain one of these values: 10min, 1h, 3h, 12h, 24h, 1w, 1m, 3m, 6m, 1y, 2y, 5y"
interface ApiService {

    @GET("exchangeCoin/all")
    suspend fun downloadExchanges(): Response<StatsResponse>

    @GET("chart")
    suspend fun getChart(
        @Query("coin") coin: String,
        @Query("exchange") exchange: String,
        @Query("currency") currency: String,
        @Query("term") term: String
    ): Response<ChartResponse>

    @GET("chart")
    fun getCurrency(
        @Query("coin") coin: String, @Query("exchange") exchange: String,
        @Query("currency") currency: String, @Query("term") term: String
    ): Observable<ChartResponse>

    @GET("chart")
    fun getSnapshot(
        @Query("coin") coin: String, @Query("exchange") exchange: String,
        @Query("currency") currency: String, @Query("term") term: String
    ): Observable<ChartResponse>
}
