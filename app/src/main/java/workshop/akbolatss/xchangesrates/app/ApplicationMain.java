package workshop.akbolatss.xchangesrates.app;

import android.app.Application;
import android.util.ArrayMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.orhanobut.hawk.Hawk;

import org.greenrobot.greendao.database.Database;
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
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import workshop.akbolatss.xchangesrates.model.ExchangeModel;
import workshop.akbolatss.xchangesrates.model.dao.DaoMaster;
import workshop.akbolatss.xchangesrates.model.dao.DaoSession;
import workshop.akbolatss.xchangesrates.networking.APIService;
import workshop.akbolatss.xchangesrates.repositories.DBOpenHelper;

import static workshop.akbolatss.xchangesrates.utils.Constants.BASE_URL;
import static workshop.akbolatss.xchangesrates.utils.Constants.DB_SNAPS_NAME;

public class ApplicationMain extends Application {

    private static final boolean ENCRYPTED = false;

    private Retrofit mRetrofit;

    private static APIService mAPIService;

    private DaoSession mDaoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        Hawk.init(getApplicationContext()).build();

        mDaoSession = provideDaoSession();

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

//        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
//                .addInterceptor(logging)
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

    private DaoSession provideDaoSession() {
        DBOpenHelper helper = new DBOpenHelper(this, DB_SNAPS_NAME);
        Database db = ENCRYPTED ? helper.getEncryptedWritableDb("super-secret") : helper.getWritableDb();
        return new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public static APIService getAPIService() {
        return mAPIService;
    }
}
