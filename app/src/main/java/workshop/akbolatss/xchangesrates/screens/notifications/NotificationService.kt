package workshop.akbolatss.xchangesrates.screens.notifications

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.widget.RemoteViews
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.app.ApplicationMain
import workshop.akbolatss.xchangesrates.model.dao.DaoMaster
import workshop.akbolatss.xchangesrates.model.dao.DaoSession
import workshop.akbolatss.xchangesrates.model.mapper.ChartDataMapper
import workshop.akbolatss.xchangesrates.repositories.DBChartRepository
import workshop.akbolatss.xchangesrates.repositories.DBOpenHelper
import workshop.akbolatss.xchangesrates.utils.Constants

/**
 * Author: Akbolat Sadvakassov
 * Date: 22.01.2018
 */

class NotificationService : Service() {

    private var mRepository: DBChartRepository? = null
    private lateinit var mContext: Context
    private var mCompositeDisposable: CompositeDisposable? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }


    override fun onCreate() {
        super.onCreate()
        mContext = this
        mRepository = DBChartRepository(provideDaoSession(mContext), ApplicationMain.apiService)
        mCompositeDisposable = CompositeDisposable()
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mRepository!!.activeChartData
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ dataList ->
                    for (i in dataList.indices) {
                        val snapshot = dataList[i]
//                        Log.d(TAG, " " + snapshot.coin + "/" + snapshot.currency)
                        mCompositeDisposable!!.add(mRepository!!.getSnapshot(dataList[i].coin,
                                dataList[i].exchange,
                                dataList[i].currency,
                                dataList[i].timing)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .map { chartResponse -> chartResponse.data }
                                .map { responseData -> ChartDataMapper(responseData, responseData.info, responseData.chart) }
                                .doAfterSuccess { chartDataMapper ->
                                    val info = chartDataMapper.dataInfo
                                    val expandedView = RemoteViews(packageName, R.layout.notification_expanded)
                                    expandedView.setTextViewText(R.id.content_title, snapshot.coin.toUpperCase() + "/" + snapshot.currency.toUpperCase() + ": " + info.last + " @ " + snapshot.exchange)
                                    expandedView.setTextViewText(R.id.content_high, info.high)
                                    expandedView.setTextViewText(R.id.content_low, info.low)
                                    expandedView.setTextViewText(R.id.content_vol, info.volume!!.toString())
                                    expandedView.setTextViewText(R.id.content_sell, info.sell)
                                    expandedView.setTextViewText(R.id.content_ch, info.change!!.toString())
                                    expandedView.setTextViewText(R.id.content_ch24, info.change24!!.toString())
                                    expandedView.setTextViewText(R.id.content_multi, info.multiply)
                                    expandedView.setTextViewText(R.id.content_started, info.started)


                                    val collapsedView = RemoteViews(packageName, R.layout.notification_collapsed)
                                    //                                            collapsedView.setImageViewResource(R.id.icon, R.mipmap.ic_main);
                                    //                                            collapsedView.setTextViewText(R.id.timestamp, DateUtils.formatDateTime(mContext, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME));
                                    collapsedView.setTextViewText(R.id.content_title, snapshot.coin.toUpperCase() + "/" + snapshot.currency.toUpperCase() + ": " + info.last + " @ " + snapshot.exchange)
                                    collapsedView.setTextViewText(R.id.content_high, info.high)
                                    collapsedView.setTextViewText(R.id.content_low, info.low)

//                                    val builder = NotificationCompat.Builder(mContext!!, resources.getString(R.string.app_name))
//                                    // these are the three things a NotificationCompat.Builder object requires at a minimum
//                                    builder.setSmallIcon(R.drawable.ic_xchange_ic)
//                                            .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
//                                            // notification will be dismissed when tapped
//                                            .setAutoCancel(true)
//                                            // setting the custom collapsed and expanded views
//                                            .setCustomContentView(collapsedView)
//                                            .setCustomBigContentView(expandedView)
//                                            .setChannelId(NOTIFICATION_CHANNEL_ID)
//                                            // setting style to DecoratedCustomViewStyle() is necessary for custom views to display
//                                            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
//                                            .setContentIntent(PendingIntent.getActivity(mContext, 0, Intent(mContext, SplashActivity::class.java), 0))
//
//                                    val notificationManager = mContext!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//                                    if (Build.VERSION.SDK_INT >= 26) {
//                                        if (notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null) {
//                                            val importance = NotificationManager.IMPORTANCE_LOW
//
//                                            val mChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance)
//
//                                            // Configure the notification channel.
//                                            mChannel.description = NOTIFICATION_CHANNEL_DESCRIPTION
//
//                                            mChannel.enableLights(true)
//                                            // Sets the notification light color for notifications posted to this
//                                            // channel, if the device supports this feature.
//                                            mChannel.lightColor = Color.GREEN
//
//                                            mChannel.enableVibration(true)
//                                            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
//
//                                            notificationManager.createNotificationChannel(mChannel)
//                                        }
//                                    }
//                                    notificationManager.notify(snapshot.id!!.toInt(), builder.build())
                                }
                                .subscribe({ mapper ->
                                    val info = mapper.dataInfo
                                    info.snapshotId = snapshot.info.snapshotId
                                    info.id = snapshot.info.id
                                    snapshot.info = info
                                    val chartsList = mapper.chartsList
                                    mRepository!!.onUpdateChartData(snapshot, info, chartsList)
                                }, {
//                                    Log.d(TAG, "onError 2 " + it.message)
                                }))
                    }
                },
                        {

                        })
        return Service.START_NOT_STICKY
    }

    private fun provideDaoSession(context: Context): DaoSession {
        return DaoMaster(DBOpenHelper(context, Constants.DB_SNAPS_NAME).writableDb).newSession()
    }

    override fun onDestroy() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable!!.clear()
        }
        super.onDestroy()
    }
}
