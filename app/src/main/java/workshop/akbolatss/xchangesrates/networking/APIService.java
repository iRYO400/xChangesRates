package workshop.akbolatss.xchangesrates.networking;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import workshop.akbolatss.xchangesrates.model.CoindeskResponse;

/**
 * Author: Akbolat Sadvakassov
 * Date: 20.11.2017
 */

public interface APIService {

    @GET("currentprice/{CODE}.json")
    Call<CoindeskResponse> getCurrencyByCode(@Path("CODE") String code);

    @GET("historical/close.json")
    Call<CoindeskResponse> getHistoricalByCode(@Query("currency") String currency, @Query("end") String startDate, @Query("start") String endDate);
}
