package workshop.akbolatss.xchangesrates.screens.splash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.orhanobut.hawk.Hawk;

import org.greenrobot.greendao.database.Database;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import workshop.akbolatss.xchangesrates.R;
import workshop.akbolatss.xchangesrates.app.ApplicationMain;
import workshop.akbolatss.xchangesrates.model.dao.DaoMaster;
import workshop.akbolatss.xchangesrates.model.dao.DaoSession;
import workshop.akbolatss.xchangesrates.model.response.ExchangeResponse;
import workshop.akbolatss.xchangesrates.repositories.DBNotificationRepository;
import workshop.akbolatss.xchangesrates.screens.main.MainActivity;

import static workshop.akbolatss.xchangesrates.utils.Constants.DB_SNAPS_NAME;
import static workshop.akbolatss.xchangesrates.utils.Constants.HAWK_EXCHANGE_RESPONSE;
import static workshop.akbolatss.xchangesrates.utils.Constants.HAWK_FIRST_START;
import static workshop.akbolatss.xchangesrates.utils.Constants.HAWK_HISTORY_CODE;
import static workshop.akbolatss.xchangesrates.utils.Constants.HAWK_HISTORY_POS;
import static workshop.akbolatss.xchangesrates.utils.Constants.HAWK_LAST_UPDATE;
import static workshop.akbolatss.xchangesrates.utils.Constants.MINUTES_10;
import static workshop.akbolatss.xchangesrates.utils.UtilityMethods.getTodayDate;

public class SplashActivity extends AppCompatActivity {

    private CountDownTimer countDownTimer;

    private Unbinder unbinder;

    private DBNotificationRepository mRepository; //TODO: Let's make a Presenter for this

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mRepository = new DBNotificationRepository(provideDaoSession(this));

        unbinder = ButterKnife.bind(this);

        countDownTimer = new CountDownTimer(700, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        };

        initDefault();
    }


    private void initDefault() {

        if (!Hawk.contains(HAWK_FIRST_START)) {
            Hawk.put(HAWK_HISTORY_POS, 0);
            Hawk.put(HAWK_HISTORY_CODE, MINUTES_10);
            Hawk.put(HAWK_FIRST_START, true);

            mRepository.initDefault()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableSingleObserver<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {
                            onUpdateXChanges();
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });
        } else {
            onUpdateXChanges();
        }
    }

    private void onUpdateXChanges() {
        ApplicationMain.getAPIService().getExchanges()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doAfterSuccess(new Consumer<ExchangeResponse>() {
                    @Override
                    public void accept(ExchangeResponse exchangeResponse) throws Exception {
                        Hawk.put(HAWK_EXCHANGE_RESPONSE, exchangeResponse);
                        Hawk.put(HAWK_LAST_UPDATE, getTodayDate());
                    }
                })
                .subscribeWith(new DisposableSingleObserver<ExchangeResponse>() {
                    @Override
                    public void onSuccess(ExchangeResponse exchangeResponse) {
                        Toast.makeText(SplashActivity.this, R.string.splash_1, Toast.LENGTH_LONG).show();
                        countDownTimer.start();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!Hawk.contains(HAWK_EXCHANGE_RESPONSE)) {
                            Toast.makeText(SplashActivity.this, R.string.splash_2, Toast.LENGTH_LONG).show();
                            countDownTimer = new CountDownTimer(1500, 1000) {
                                @Override
                                public void onTick(long l) {

                                }

                                @Override
                                public void onFinish() {
                                    finish();
                                }
                            }.start();
                        } else {
                            String s = Hawk.get(HAWK_LAST_UPDATE, "");
                            Toast.makeText(SplashActivity.this, R.string.splash_3 + " " + s, Toast.LENGTH_LONG).show();
                            countDownTimer.start();
                        }
                    }
                });
    }

    private DaoSession provideDaoSession(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_SNAPS_NAME);
        Database db = helper.getWritableDb();
        return new DaoMaster(db).newSession();
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
