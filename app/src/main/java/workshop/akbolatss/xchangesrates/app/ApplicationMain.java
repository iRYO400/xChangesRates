package workshop.akbolatss.xchangesrates.app;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.orhanobut.hawk.Hawk;

import java.lang.reflect.Type;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import workshop.akbolatss.xchangesrates.model.BPIResponse;
import workshop.akbolatss.xchangesrates.networking.APIService;

import static workshop.akbolatss.xchangesrates.utils.Constants.BASE_URL;

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
                .registerTypeAdapter(BPIResponse.class, new JsonDeserializer<BPIResponse>() {
                    @Override
                    public BPIResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

                        if (json.isJsonPrimitive()) {
                            return new BPIResponse((float) json.getAsDouble());
                        } else {
                            return new Gson().fromJson(json, BPIResponse.class);
                        }
                    }
                })
                .create();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        mAPIService = mRetrofit.create(APIService.class);
    }

    public static APIService getAPIService() {
        return mAPIService;
    }
}
