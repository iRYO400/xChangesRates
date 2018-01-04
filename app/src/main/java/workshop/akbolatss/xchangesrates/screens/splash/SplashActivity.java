package workshop.akbolatss.xchangesrates.screens.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.orhanobut.hawk.Hawk;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import workshop.akbolatss.xchangesrates.R;
import workshop.akbolatss.xchangesrates.app.ApplicationMain;
import workshop.akbolatss.xchangesrates.model.ExchangeResponse;
import workshop.akbolatss.xchangesrates.screens.main.MainActivity;

import static workshop.akbolatss.xchangesrates.utils.Constants.HAWK_EXCHANGE_RESPONSE;
import static workshop.akbolatss.xchangesrates.utils.Constants.HAWK_FIRST_FRAG;
import static workshop.akbolatss.xchangesrates.utils.Constants.HAWK_HISTORY_CODE;
import static workshop.akbolatss.xchangesrates.utils.Constants.HAWK_HISTORY_POS;
import static workshop.akbolatss.xchangesrates.utils.Constants.HAWK_LAST_UPDATE;
import static workshop.akbolatss.xchangesrates.utils.UtilityMethods.getTodayDate;

public class SplashActivity extends AppCompatActivity {

    CountDownTimer countDownTimer;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        unbinder = ButterKnife.bind(this);

        countDownTimer = new CountDownTimer(1500, 1000) {
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

        if (!Hawk.contains(HAWK_FIRST_FRAG)) {
            Hawk.put(HAWK_HISTORY_POS, 0);
            Hawk.put(HAWK_HISTORY_CODE, "10min");
            Hawk.put(HAWK_FIRST_FRAG, true);
        }

        ApplicationMain.getAPIService().getExchanges().enqueue(new Callback<ExchangeResponse>() {
            @Override
            public void onResponse(Call<ExchangeResponse> call, Response<ExchangeResponse> response) {

                Hawk.put(HAWK_EXCHANGE_RESPONSE, response.body());
                String s = getTodayDate();
                Hawk.put(HAWK_LAST_UPDATE, s);

                Toast.makeText(SplashActivity.this, "Data updated successfully", Toast.LENGTH_SHORT).show();
                countDownTimer.start();
            }

            @Override
            public void onFailure(Call<ExchangeResponse> call, Throwable t) {
                if (!Hawk.contains(HAWK_EXCHANGE_RESPONSE)) {

                    Toast.makeText(SplashActivity.this, "Check your connection and restart app.", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(SplashActivity.this, "Can't update. Last update was " + s, Toast.LENGTH_SHORT).show();
                    countDownTimer.start();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
