package workshop.akbolatss.xchangesrates.screens.notifications

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.orhanobut.hawk.Hawk
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.app.ApplicationMain
import workshop.akbolatss.xchangesrates.model.response.ChartData
import workshop.akbolatss.xchangesrates.repositories.DBChartRepository
import workshop.akbolatss.xchangesrates.utils.Constants.HAWK_SHOULD_OFF
import workshop.akbolatss.xchangesrates.utils.Constants.NOTIFICATION_CHANNEL_DESCRIPTION
import workshop.akbolatss.xchangesrates.utils.Constants.NOTIFICATION_CHANNEL_ID
import workshop.akbolatss.xchangesrates.utils.Constants.NOTIFICATION_CHANNEL_NAME
import workshop.akbolatss.xchangesrates.utils.Constants.NOTIFICATION_GROUP_ID
import workshop.akbolatss.xchangesrates.utils.Constants.NOTIFICATION_MAIN_ID
import workshop.akbolatss.xchangesrates.utils.Logger
import workshop.akbolatss.xchangesrates.utils.UtilityMethods.calculateInterval
import workshop.akbolatss.xchangesrates.utils.UtilityMethods.generateChannelId
import java.util.concurrent.TimeUnit

/**
 * Author: Akbolat Sadvakassov
 * Date: 22.01.2018
 */

class NotificationService : Service() {

    private lateinit var mRepository: DBChartRepository
    private lateinit var mCompositeDisposable: CompositeDisposable
    private lateinit var mNotificationManager: NotificationManagerCompat

    companion object {
        /**
         * Check if this service still running
         */
        var isRunning = false

        var isDone = false
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Logger.i("onCreate Service")
        mRepository = DBChartRepository(ApplicationMain.apiService,
                ApplicationMain.instance.appDatabase.chartDataDao())
        mCompositeDisposable = CompositeDisposable()
        mNotificationManager = NotificationManagerCompat.from(this)
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        isRunning = true
        Logger.i("onStartCommand")

        startForeground(NOTIFICATION_MAIN_ID, generateForegroundNotification())

        startWork()
        return Service.START_STICKY
    }

    private fun generateForegroundNotification(): Notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(NOTIFICATION_CHANNEL_NAME)
                .setContentText(NOTIFICATION_CHANNEL_DESCRIPTION)
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.ic_xchange_ic)
                .setGroup(NOTIFICATION_GROUP_ID)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .build()


    private fun startWork() {
        mCompositeDisposable.clear()
        if (mCompositeDisposable.isDisposed) {
            mCompositeDisposable = CompositeDisposable()
        }
        mCompositeDisposable.add(mRepository.allActive
                .subscribeOn(Schedulers.io())
                .toObservable()
                .flatMapIterable {
                    it
                }
                .subscribe({
                    executeTimers(it)
                }, {
                    it.printStackTrace()
                })
        )
    }

    private fun executeTimers(chartData: ChartData) {
        Logger.i(generateChannelId(chartData) + " is being executed and the intervalIndex is ${chartData.options.intervalUpdateIndex} == ${calculateInterval(chartData.options.intervalUpdateIndex)}")
        val disp = pushNotify(chartData).zipWith(Observable.interval(calculateInterval(chartData.options.intervalUpdateIndex), TimeUnit.SECONDS),
                BiFunction<ChartData, Long, ChartData> { cd: ChartData, timer: Long ->
                    cd
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .flatMap { cd ->
                    fetchUpdate(cd)
                }
                .doOnNext { cd ->
                    Logger.i(generateChannelId(chartData) + "'s delay is: ${calculateInterval(cd.options.intervalUpdateIndex)}")
                    Logger.i("Running state: $isRunning")
                }
                .repeat()
                .subscribe({
                    Logger.i("After Debounce ...")
                    Logger.i("subscribe done")
                }, {
                    it.printStackTrace()
                    Logger.e("Error in onStartCommand: ${it.message}")
                })

        mCompositeDisposable.add(disp)
    }

    private fun fetchUpdate(chartData: ChartData): Observable<ChartData> {
        Logger.i("Fetching updates...")
        return mRepository.getSnapshot(
                chartData.coin,
                chartData.exchange,
                chartData.currency,
                chartData.timingName)
                .subscribeOn(Schedulers.io())
                .map { chartResponse ->
                    chartResponse.data
                }
                .map { newChartData ->
                    newChartData.id = chartData.id
                    newChartData.coin = chartData.coin
                    newChartData.timingName = chartData.timingName
                    newChartData.timingIndex = chartData.timingIndex
                    newChartData.isNotificationEnabled = chartData.isNotificationEnabled
                    newChartData.options = chartData.options
                    Logger.i("Fetching done")
                    newChartData
                }
                .flatMap { newChartData ->
                    mRepository.onUpdateChartData(newChartData)
                }
    }

    private fun pushNotify(chartData: ChartData): Observable<ChartData> {
        return Observable.fromCallable {
            if (chartData.isNotificationEnabled) {
                val inboxStyle = NotificationCompat.InboxStyle()
                inboxStyle.addLine("High: ${chartData.info.high} | Low: ${chartData.info.low}")
                inboxStyle.addLine("Buy: ${chartData.info.buy} | Sell: ${chartData.info.sell}")
                inboxStyle.addLine("Change: ${chartData.info.change} | Change24: ${chartData.info.change24}")
                inboxStyle.addLine("Volume: ${chartData.info.volume}")


                val builder = NotificationCompat.Builder(this, generateChannelId(chartData))

                        .setContentTitle("${chartData.coin.toUpperCase()}/${chartData.currency.toUpperCase()}: ${chartData.info.last} @ ${chartData.exchange}")
                        .setContentText("${chartData.info.last}")
                        .setAutoCancel(false)
                        .setStyle(inboxStyle)
                        .setSmallIcon(R.drawable.ic_xchange_ic)
                        .setGroup(NOTIFICATION_GROUP_ID)
                        .setOngoing(true)
                if (chartData.options.isStickedEnabled) {
                    builder.priority = NotificationCompat.PRIORITY_LOW
                } else {
                    builder.priority = NotificationCompat.PRIORITY_MIN
                }

                mNotificationManager.notify(chartData.id.toInt(), builder.build())
                chartData
            } else {
                mNotificationManager.cancel(chartData.id.toInt())
                chartData
            }
        }
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun onDestroy() {
        if (::mCompositeDisposable.isInitialized) {
            mCompositeDisposable.clear()
        }
        if (!Hawk.get(HAWK_SHOULD_OFF, false)) {
            val broadcastIntent = Intent(applicationContext, NotificationReceiver::class.java)
            sendBroadcast(broadcastIntent)
        }
        isRunning = false
        super.onDestroy()
    }
}
