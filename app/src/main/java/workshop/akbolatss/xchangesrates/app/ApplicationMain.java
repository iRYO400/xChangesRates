package workshop.akbolatss.xchangesrates.app;

import android.app.Application;
import android.util.ArrayMap;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.orhanobut.hawk.Hawk;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import me.yokeyword.fragmentation.BuildConfig;
import me.yokeyword.fragmentation.Fragmentation;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import workshop.akbolatss.xchangesrates.model.ExchangeModel;
import workshop.akbolatss.xchangesrates.networking.APIService;

import static workshop.akbolatss.xchangesrates.utils.Constants.BASE_URL;
import static workshop.akbolatss.xchangesrates.utils.Constants.DEBUG_TAG;

/**
 * Author: Akbolat Sadvakassov
 * Date: 20.11.2017
 */

public class ApplicationMain extends Application {

    private Retrofit mRetrofit;

    private static APIService mAPIService;

    @Override
    public void onCreate() {
        super.onCreate();

        Hawk.init(getApplicationContext()).build();

        // Gson кастомизация
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ExchangeModel.class, new JsonDeserializer<ExchangeModel>() {
                    @Override
                    public ExchangeModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        ExchangeModel exchangeModel = null;
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(json.toString());
                            Iterator iterator = jsonObject.keys();
                            iterator.next(); // skip id
                            iterator.next(); // skip caption

                            Map<String, List<String>> listMap = new ArrayMap<>();
                            List<String> buffCurrency = null;
                            String buff = "";
                            while (iterator.hasNext()) {
                                buff = iterator.next().toString();

                                buffCurrency = new ArrayList<>();
                                for (int i = 0; i < jsonObject.getJSONArray(buff).length(); i++) {
                                    buffCurrency.add(jsonObject.getJSONArray(buff).getString(i));
                                }
                                listMap.put(buff, buffCurrency);
                            }

                            exchangeModel = new ExchangeModel(
                                    jsonObject.getString("ident"),
                                    jsonObject.getString("caption"),
                                    listMap);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        return exchangeModel;
                    }
                })
                .create();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();

        Fragmentation.builder()
                .stackViewMode(Fragmentation.BUBBLE)
                .debug(BuildConfig.DEBUG)
                .install();

        mAPIService = mRetrofit.create(APIService.class);
    }

    public static APIService getAPIService() {
        return mAPIService;
    }
}
