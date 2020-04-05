package workshop.akbolatss.xchangesrates.screens.splash

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.orhanobut.hawk.Hawk
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.app.ApplicationMain
import workshop.akbolatss.xchangesrates.model.response.ExchangeResponse
import workshop.akbolatss.xchangesrates.networking.APIService
import workshop.akbolatss.xchangesrates.screens.MainActivity
import workshop.akbolatss.xchangesrates.utils.Constants
import workshop.akbolatss.xchangesrates.utils.UtilityMethods

class SplashActivity : AppCompatActivity() {

    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        countDownTimer = object : CountDownTimer(700, 1000) {
            override fun onTick(l: Long) {}

            override fun onFinish() {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }
        }

        initDefault()
    }

    private fun initDefault() {
        if (!Hawk.contains(Constants.HAWK_FIRST_START)) {
            Hawk.put(Constants.HAWK_HISTORY_POS, 0)
            Hawk.put(Constants.HAWK_HISTORY_CODE, Constants.MINUTES_10)
            Hawk.put(Constants.HAWK_FIRST_START, true)
            onUpdateXChanges()
        } else {
            onUpdateXChanges()
        }
    }

    private fun onUpdateXChanges() {
        val apiService = ApplicationMain.getRetrofit().create(APIService::class.java)

        apiService.getExchanges()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doAfterSuccess { exchangeResponse ->
                    Hawk.put(Constants.HAWK_EXCHANGE_RESPONSE, exchangeResponse)
                    Hawk.put(Constants.HAWK_LAST_UPDATE, UtilityMethods.todayDate)
                }
                .subscribe(object : DisposableSingleObserver<ExchangeResponse>() {
                    override fun onSuccess(exchangeResponse: ExchangeResponse) {
                        Toast.makeText(this@SplashActivity, R.string.splash_1, Toast.LENGTH_LONG).show()
                        countDownTimer!!.start()
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        if (!Hawk.contains(Constants.HAWK_EXCHANGE_RESPONSE)) {
                            Toast.makeText(this@SplashActivity, R.string.splash_2, Toast.LENGTH_LONG).show()
                            countDownTimer = object : CountDownTimer(1500, 1000) {
                                override fun onTick(l: Long) {

                                }

                                override fun onFinish() {
                                    finish()
                                }
                            }.start()
                        } else {
                            val s = Hawk.get(Constants.HAWK_LAST_UPDATE, "")
                            Toast.makeText(this@SplashActivity, R.string.splash_3.toString() + " " + s, Toast.LENGTH_LONG).show()
                            countDownTimer!!.start()
                        }
                    }
                })
    }
}
