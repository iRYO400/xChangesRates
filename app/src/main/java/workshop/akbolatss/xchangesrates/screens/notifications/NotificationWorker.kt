package workshop.akbolatss.xchangesrates.screens.notifications

import android.content.Context
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.app.ApplicationMain
import workshop.akbolatss.xchangesrates.model.response.ChartData
import workshop.akbolatss.xchangesrates.repositories.DBChartRepository
import workshop.akbolatss.xchangesrates.utils.Constants
import workshop.akbolatss.xchangesrates.utils.Logger
import workshop.akbolatss.xchangesrates.utils.UtilityMethods

class NotificationWorker(private val appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {

    private lateinit var mRepository: DBChartRepository
    private lateinit var mCompositeDisposable: CompositeDisposable
    private lateinit var mNotificationManager: NotificationManagerCompat

    override fun doWork(): Result {
        mRepository = DBChartRepository(appContext)
        mCompositeDisposable = CompositeDisposable()
        mNotificationManager = NotificationManagerCompat.from(applicationContext)

        doTheJob()

        return Result.success()
    }

    private fun doTheJob() {
        val id = inputData.getLong(Constants.WORKER_INPUT_ID, 0)
        Logger.i("StartWork. Id of chartData is $id")
        mCompositeDisposable.add(mRepository.onGetById(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .toObservable()
                .flatMap { cd ->
                    fetchUpdate(cd)
                }
                .flatMap {
                    pushNotify(it)
                }
                .subscribe({
                    mCompositeDisposable.clear()
                }, {
                    it.printStackTrace()
                })
        )
    }

    private fun pushNotify(chartData: ChartData): Observable<ChartData> {
        return Observable.fromCallable {
            if (chartData.isNotificationEnabled) {
                val inboxStyle = NotificationCompat.InboxStyle()
                inboxStyle.addLine("High: ${chartData.info.high} | Low: ${chartData.info.low}")
                inboxStyle.addLine("Buy: ${chartData.info.buy} | Sell: ${chartData.info.sell}")
                inboxStyle.addLine("Change: ${chartData.info.change} | Change24: ${chartData.info.change24}")
                inboxStyle.addLine("Volume: ${chartData.info.volume}")

                val builder = NotificationCompat.Builder(applicationContext, UtilityMethods.generateChannelId(chartData))

                        .setContentTitle("${chartData.coin.toUpperCase()}/${chartData.currency.toUpperCase()}: ${chartData.info.last} @ ${chartData.exchange}")
                        .setContentText("${chartData.info.last}")
                        .setAutoCancel(false)
                        .setStyle(inboxStyle)
                        .setSmallIcon(R.drawable.ic_xchange_ic)
                        .setGroup(Constants.NOTIFICATION_GROUP_ID)
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

    private fun fetchUpdate(chartData: ChartData): Observable<ChartData> {
        Logger.i("Worker. Fetching updates...")
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
                    Logger.i("Worker. Fetching done")
                    newChartData
                }
                .flatMap { newChartData ->
                    mRepository.onUpdateChartData(newChartData)
                }
    }
}