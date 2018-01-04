package workshop.akbolatss.xchangesrates.networking;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import workshop.akbolatss.xchangesrates.model.ChartResponse;
import workshop.akbolatss.xchangesrates.model.ExchangeResponse;

/**
 * Author: Akbolat Sadvakassov
 * Date: 20.11.2017
 */

public interface APIService {

    @GET("chart")
    Call<ChartResponse> getCurrency(@Query("coin") String coin, @Query("exchange") String exchange,
                                    @Query("currency") String currency, @Query("term") String term);
    //"The Term field needs to contain one of these values: 10min, 1h, 3h, 12h, 24h, 1w, 1m, 3m, 6m, 1y, 2y, 5y"

    @GET("exchangeCoin/all")
    Call<ExchangeResponse> getExchanges();

    @GET("chart")
    Single<ChartResponse> getSnapshot(@Query("coin") String coin, @Query("exchange") String exchange,
                                      @Query("currency") String currency, @Query("term") String term);
}
