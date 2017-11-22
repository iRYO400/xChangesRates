package workshop.akbolatss.xchangesrates.screens.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.orhanobut.hawk.Hawk;


import workshop.akbolatss.xchangesrates.R;
import workshop.akbolatss.xchangesrates.screens.main.MainActivity;

import static workshop.akbolatss.xchangesrates.utils.Constants.DEBUG_TAG;
import static workshop.akbolatss.xchangesrates.utils.Constants.HAWK_CURRENCY_POS;
import static workshop.akbolatss.xchangesrates.utils.Constants.HAWK_CURRENCY_RATE;
import static workshop.akbolatss.xchangesrates.utils.Constants.HAWK_FIRST_FRAG;
import static workshop.akbolatss.xchangesrates.utils.Constants.HAWK_HISTORY;
import static workshop.akbolatss.xchangesrates.utils.Constants.WEEK_AGO;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initDefault();
    }

    private void initDefault() {
        if (!Hawk.contains(HAWK_FIRST_FRAG)) {
            Hawk.put(HAWK_FIRST_FRAG, true);
            Hawk.put(HAWK_CURRENCY_RATE, 0.0f);
            Hawk.put(HAWK_CURRENCY_POS, 0);
            Hawk.put(HAWK_HISTORY, WEEK_AGO);
        }
        new CountDownTimer(1500, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }.start();
    }
}
