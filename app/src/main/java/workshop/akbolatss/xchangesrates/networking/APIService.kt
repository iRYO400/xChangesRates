package workshop.akbolatss.xchangesrates.networking

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import workshop.akbolatss.xchangesrates.model.response.ChartResponse
import workshop.akbolatss.xchangesrates.model.response.ExchangeResponse

/**
 * Author: Akbolat Sadvakassov
 * Date: 20.11.2017
 */

interface APIService {
    //"The Term field needs to contain one of these values: 10min, 1h, 3h, 12h, 24h, 1w, 1m, 3m, 6m, 1y, 2y, 5y"

    @GET("exchangeCoin/all")
    fun getExchanges(): Single<ExchangeResponse>

    @GET("chart")
    fun getCurrency(@Query("coin") coin: String, @Query("exchange") exchange: String,
                    @Query("currency") currency: String, @Query("term") term: String): Observable<ChartResponse>

    @GET("chart")
    fun getSnapshot(@Query("coin") coin: String, @Query("exchange") exchange: String,
                    @Query("currency") currency: String, @Query("term") term: String): Observable<ChartResponse>
}
