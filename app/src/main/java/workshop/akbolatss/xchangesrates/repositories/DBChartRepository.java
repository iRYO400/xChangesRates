package workshop.akbolatss.xchangesrates.repositories;

import android.util.Log;

import org.greenrobot.greendao.query.DeleteQuery;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import workshop.akbolatss.xchangesrates.model.dao.ChartData;
import workshop.akbolatss.xchangesrates.model.dao.ChartDataCharts;
import workshop.akbolatss.xchangesrates.model.dao.ChartDataChartsDao;
import workshop.akbolatss.xchangesrates.model.dao.ChartDataDao;
import workshop.akbolatss.xchangesrates.model.dao.ChartDataInfo;
import workshop.akbolatss.xchangesrates.model.dao.ChartDataInfoDao;
import workshop.akbolatss.xchangesrates.model.dao.DaoSession;
import workshop.akbolatss.xchangesrates.model.response.ChartResponse;
import workshop.akbolatss.xchangesrates.networking.APIService;

/**
 * Author: Akbolat Sadvakassov
 * Date: 04.01.2018
 */

public class DBChartRepository implements ChartRepository {

    private DaoSession mDaoSession;
    private APIService mApiService;


    public DBChartRepository(DaoSession mDaoSession, APIService mApiService) {
        this.mDaoSession = mDaoSession;
        this.mApiService = mApiService;
    }

    @Override
    public Single<ChartResponse> getQueryResult(String coin, String exchange, String currency, String term) {
        return mApiService.getCurrency(coin, exchange, currency, term);
    }

    @Override
    public Single<List<ChartData>> getAllChartData() {
        return Single.fromCallable(new Callable<List<ChartData>>() {
            @Override
            public List<ChartData> call() throws Exception {
                ChartDataDao chartDataDao = mDaoSession.getChartDataDao();
                return chartDataDao.loadAll();
            }
        });
    }

    @Override
    public Single<List<ChartData>> getActiveChartData() {
        return Single.fromCallable(new Callable<List<ChartData>>() {
            @Override
            public List<ChartData> call() throws Exception {
                ChartDataDao dataDao = mDaoSession.getChartDataDao();
                return dataDao.queryBuilder().where(ChartDataDao.Properties.IsActive.eq(true)).list();
            }
        });
    }

    @Override
    public Single<ChartDataInfo> getChartDataInfo(final long key) {
        return Single.fromCallable(new Callable<ChartDataInfo>() {
            @Override
            public ChartDataInfo call() throws Exception {
                ChartDataInfoDao infoDao = mDaoSession.getChartDataInfoDao();
                return infoDao.load(key);
            }
        });
    }

    @Override
    public Single<ChartResponse> getSnapshot(String coin, String exchange, String currency, String term) {
        return mApiService.getSnapshot(coin, exchange, currency, term);
    }

    @Override
    public Single<Boolean> onAddChartData(final ChartData data, final ChartDataInfo dataInfo, final List<ChartDataCharts> chartsList) {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {

                ChartDataDao chartDataDao = mDaoSession.getChartDataDao();
                long dataId = chartDataDao.insert(data);

                ChartDataInfoDao infoDao = mDaoSession.getChartDataInfoDao();
                dataInfo.setInfoId(dataId);
                infoDao.insert(dataInfo);

                ChartDataChartsDao dataChartsDao = mDaoSession.getChartDataChartsDao();
                for (int i = 0; i < chartsList.size(); i++) {
                    chartsList.get(i).setChartsId(dataId);
                    dataChartsDao.insert(chartsList.get(i));
                }

//                ChartData newData = chartDataDao.load(dataId);
//
//                try {
//                    Log.d("TAG", "NewData " + newData.getInfo().getInfoId() + " newDataId " + newData.getId());
//                } catch (NullPointerException e) {
//                    e.printStackTrace();
//                }
                return true;
            }
        });
    }

    @Override
    public void onUpdateChartData(final ChartData data, final ChartDataInfo dataInfo, final List<ChartDataCharts> chartsList) {
        Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                ChartDataInfoDao infoDao = mDaoSession.getChartDataInfoDao();
                infoDao.update(dataInfo);

                ChartDataDao chartDataDao = mDaoSession.getChartDataDao();
                chartDataDao.update(data);

                ChartDataChartsDao dataChartsDao = mDaoSession.getChartDataChartsDao();
                DeleteQuery<ChartDataCharts> deleteQuery = dataChartsDao.queryBuilder()
                        .where(ChartDataChartsDao.Properties.ChartsId.eq(data.getId()))
                        .buildDelete();
                deleteQuery.executeDeleteWithoutDetachingEntities();

                for (int i = 0; i < chartsList.size(); i++) {
                    chartsList.get(i).setChartsId(data.getId());
                    dataChartsDao.insert(chartsList.get(i));
                }
                return true;
            }
        })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    @Override
    public Single<Integer> onOptionsChanged(final long key, final boolean isActive, final String timing) {
        return Single.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                ChartDataDao dataDao = mDaoSession.getChartDataDao();
                ChartData data = dataDao.load(key);
                data.setIsActive(isActive);
                data.setTiming(timing);
                dataDao.update(data);

                return dataDao.queryBuilder().where(ChartDataDao.Properties.IsActive.eq(true)).list().size();
            }
        });
    }

    @Override
    public Single<Integer> onDeleteChartData(final long chartId) {
        return Single.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                ChartDataChartsDao dataChartsDao = mDaoSession.getChartDataChartsDao();
                DeleteQuery<ChartDataCharts> deleteQuery = dataChartsDao.queryBuilder()
                        .where(ChartDataChartsDao.Properties.ChartsId.eq(chartId))
                        .buildDelete();
                deleteQuery.executeDeleteWithoutDetachingEntities();

                ChartDataInfoDao infoDao = mDaoSession.getChartDataInfoDao();
                DeleteQuery<ChartDataInfo> deleteQuery1 = infoDao.queryBuilder()
                        .where(ChartDataInfoDao.Properties.InfoId.eq(chartId)).buildDelete();
                deleteQuery1.executeDeleteWithoutDetachingEntities();

                ChartDataDao chartDataDao = mDaoSession.getChartDataDao();
                chartDataDao.deleteByKey(chartId);

                return chartDataDao.queryBuilder().where(ChartDataDao.Properties.IsActive.eq(true)).list().size();
            }
        });
    }
}
