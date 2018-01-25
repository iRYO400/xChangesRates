package workshop.akbolatss.xchangesrates.screens.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import org.greenrobot.greendao.database.Database;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import workshop.akbolatss.xchangesrates.R;
import workshop.akbolatss.xchangesrates.app.ApplicationMain;
import workshop.akbolatss.xchangesrates.model.dao.ChartData;
import workshop.akbolatss.xchangesrates.model.dao.ChartDataCharts;
import workshop.akbolatss.xchangesrates.model.dao.ChartDataInfo;
import workshop.akbolatss.xchangesrates.model.dao.DaoMaster;
import workshop.akbolatss.xchangesrates.model.dao.DaoSession;
import workshop.akbolatss.xchangesrates.model.mapper.ChartDataMapper;
import workshop.akbolatss.xchangesrates.model.response.ChartResponse;
import workshop.akbolatss.xchangesrates.model.response.ChartResponseData;
import workshop.akbolatss.xchangesrates.repositories.DBChartRepository;
import workshop.akbolatss.xchangesrates.screens.splash.SplashActivity;

import static workshop.akbolatss.xchangesrates.utils.Constants.DB_SNAPS_NAME;

/**
 * Author: Akbolat Sadvakassov
 * Date: 22.01.2018
 */

public class NotificationService extends Service {

    private static final String TAG = "NotificationService";
    public static final String NOTIFICATION_CHANNEL_ID = "workshop.akbolatss.xchangesrates.channel";
    public static final String NOTIFICATION_CHANNEL_NAME = "XChanges Rates";
    public static final String NOTIFICATION_CHANNEL_DESCRIPTION = "Description of XChanges Rates";

    private DBChartRepository mRepository;
    private Context mContext;
    private CompositeDisposable mCompositeDisposable;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        mRepository = new DBChartRepository(provideDaoSession(mContext), ApplicationMain.getAPIService());
        mCompositeDisposable = new CompositeDisposable();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mRepository.getActiveChartData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new DisposableSingleObserver<List<ChartData>>() {
                    @Override
                    public void onSuccess(final List<ChartData> dataList) {
                        for (int i = 0; i < dataList.size(); i++) {
                            final ChartData chartData = dataList.get(i);
                            Log.d(TAG, " " + chartData.getCoin() + "/" + chartData.getCurrency());
                            mCompositeDisposable.add(mRepository.getSnapshot(dataList.get(i).getCoin(),
                                    dataList.get(i).getExchange(),
                                    dataList.get(i).getCurrency(),
                                    dataList.get(i).getTiming())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .map(new Function<ChartResponse, ChartResponseData>() {
                                        @Override
                                        public ChartResponseData apply(ChartResponse chartResponse) throws Exception {
                                            return chartResponse.getData();
                                        }
                                    })
                                    .map(new Function<ChartResponseData, ChartDataMapper>() {
                                        @Override
                                        public ChartDataMapper apply(ChartResponseData responseData) throws Exception {
                                            return new ChartDataMapper(responseData, responseData.getInfo(), responseData.getChart());
                                        }
                                    })
                                    .doAfterSuccess(new Consumer<ChartDataMapper>() {
                                        @Override
                                        public void accept(ChartDataMapper chartDataMapper) throws Exception {
                                            Log.d(TAG, "onSuccess 2");
                                            ChartDataInfo info = chartDataMapper.getDataInfo();
                                            RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.notification_expanded);
                                            expandedView.setTextViewText(R.id.content_title, chartData.getCoin().toUpperCase() + "/" + chartData.getCurrency().toUpperCase() + ": " + info.getLast() + " @ " + chartData.getExchange());
                                            expandedView.setTextViewText(R.id.content_high, info.getHigh());
                                            expandedView.setTextViewText(R.id.content_low, info.getLow());
                                            expandedView.setTextViewText(R.id.content_vol, info.getVolume().toString());
                                            expandedView.setTextViewText(R.id.content_sell, info.getSell());
                                            expandedView.setTextViewText(R.id.content_ch, info.getChange().toString());
                                            expandedView.setTextViewText(R.id.content_ch24, info.getChange24().toString());
                                            expandedView.setTextViewText(R.id.content_multi, info.getMultiply());
                                            expandedView.setTextViewText(R.id.content_started, info.getStarted());


                                            RemoteViews collapsedView = new RemoteViews(getPackageName(), R.layout.notification_collapsed);
//                                            collapsedView.setImageViewResource(R.id.icon, R.mipmap.ic_main);
//                                            collapsedView.setTextViewText(R.id.timestamp, DateUtils.formatDateTime(mContext, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME));
                                            collapsedView.setTextViewText(R.id.content_title, chartData.getCoin().toUpperCase() + "/" + chartData.getCurrency().toUpperCase() + ": " + info.getLast() + " @ " + chartData.getExchange());
                                            collapsedView.setTextViewText(R.id.content_high, info.getHigh());
                                            collapsedView.setTextViewText(R.id.content_low, info.getLow());

                                            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, getResources().getString(R.string.app_name));
                                            // these are the three things a NotificationCompat.Builder object requires at a minimum
                                            builder.setSmallIcon(R.drawable.ic_xchange_ic)
                                                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                                                    // notification will be dismissed when tapped
                                                    .setAutoCancel(true)
                                                    // setting the custom collapsed and expanded views
                                                    .setCustomContentView(collapsedView)
                                                    .setCustomBigContentView(expandedView)
                                                    .setChannelId(NOTIFICATION_CHANNEL_ID)
                                                    // setting style to DecoratedCustomViewStyle() is necessary for custom views to display
                                                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                                                    .setContentIntent(PendingIntent.getActivity(mContext, 0, new Intent(mContext, SplashActivity.class), 0));

                                            NotificationManager notificationManager = (NotificationManager)
                                                    mContext.getSystemService(Context.NOTIFICATION_SERVICE);

                                            if (Build.VERSION.SDK_INT >= 26) {
                                                if (notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null) {
                                                    int importance = NotificationManager.IMPORTANCE_LOW;

                                                    NotificationChannel mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);

                                                    // Configure the notification channel.
                                                    mChannel.setDescription(NOTIFICATION_CHANNEL_DESCRIPTION);

                                                    mChannel.enableLights(true);
                                                    // Sets the notification light color for notifications posted to this
                                                    // channel, if the device supports this feature.
                                                    mChannel.setLightColor(Color.GREEN);

                                                    mChannel.enableVibration(true);
                                                    mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

                                                    notificationManager.createNotificationChannel(mChannel);
                                                }
                                            }
                                            notificationManager.notify(chartData.getId().intValue(), builder.build());
                                        }
                                    })
                                    .subscribeWith(new DisposableSingleObserver<ChartDataMapper>() {
                                        @Override
                                        public void onSuccess(ChartDataMapper mapper) {
                                            ChartDataInfo dataInfo = mapper.getDataInfo();
                                            dataInfo.setInfoId(chartData.getInfo().getInfoId());
                                            dataInfo.setId(chartData.getInfo().getId());
                                            chartData.setChartDataInfo(dataInfo);
                                            List<ChartDataCharts> chartsList = mapper.getChartsList();
                                            mRepository.onUpdateChartData(chartData, dataInfo, chartsList);
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            Log.d(TAG, "onError 2 " + e.getMessage());
                                        }
                                    }));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError 1. " + e.getMessage() + " . " + e.getLocalizedMessage());
                    }
                });
        return START_NOT_STICKY;
    }

    private DaoSession provideDaoSession(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_SNAPS_NAME);
        Database db = helper.getWritableDb();
        return new DaoMaster(db).newSession();
    }

    @Override
    public void onDestroy() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
        super.onDestroy();
    }
}
