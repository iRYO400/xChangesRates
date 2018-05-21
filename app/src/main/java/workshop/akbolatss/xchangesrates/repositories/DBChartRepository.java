package workshop.akbolatss.xchangesrates.repositories;

import org.greenrobot.greendao.query.DeleteQuery;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import workshop.akbolatss.xchangesrates.model.dao.DaoSession;
import workshop.akbolatss.xchangesrates.model.dao.Snapshot;
import workshop.akbolatss.xchangesrates.model.dao.SnapshotChart;
import workshop.akbolatss.xchangesrates.model.dao.SnapshotChartDao;
import workshop.akbolatss.xchangesrates.model.dao.SnapshotDao;
import workshop.akbolatss.xchangesrates.model.dao.SnapshotInfo;
import workshop.akbolatss.xchangesrates.model.dao.SnapshotInfoDao;
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
    public Single<List<Snapshot>> getAllChartData() {
        return Single.fromCallable(new Callable<List<Snapshot>>() {
            @Override
            public List<Snapshot> call() throws Exception {
                SnapshotDao snapshotDao = mDaoSession.getSnapshotDao();
                return snapshotDao.loadAll();
            }
        });
    }

    @Override
    public Single<List<Snapshot>> getActiveChartData() {
        return Single.fromCallable(new Callable<List<Snapshot>>() {
            @Override
            public List<Snapshot> call() throws Exception {
                SnapshotDao dataDao = mDaoSession.getSnapshotDao();
                return dataDao.queryBuilder().where(SnapshotDao.Properties.IsActiveForGlobal.eq(true)).list();
            }
        });
    }

    @Override
    public Single<SnapshotInfo> getChartDataInfo(final long key) {
        return Single.fromCallable(new Callable<SnapshotInfo>() {
            @Override
            public SnapshotInfo call() throws Exception {
                SnapshotInfoDao infoDao = mDaoSession.getSnapshotInfoDao();
                return infoDao.load(key);
            }
        });
    }

    @Override
    public Single<ChartResponse> getSnapshot(String coin, String exchange, String currency, String term) {
        return mApiService.getSnapshot(coin, exchange, currency, term);
    }

    @Override
    public Single<Boolean> onAddChartData(final Snapshot snapshot, final SnapshotInfo info, final List<SnapshotChart> chartsList) {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {

                SnapshotDao chartDataDao = mDaoSession.getSnapshotDao();
                long dataId = chartDataDao.insert(snapshot);

                SnapshotInfoDao infoDao = mDaoSession.getSnapshotInfoDao();
                info.setSnapshotId(dataId);
                infoDao.insert(info);

                SnapshotChartDao dataChartsDao = mDaoSession.getSnapshotChartDao();
                for (int i = 0; i < chartsList.size(); i++) {
                    chartsList.get(i).setSnapshotId(dataId);
                    dataChartsDao.insert(chartsList.get(i));
                }

//                Snapshot newData = chartDataDao.load(dataId);
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
    public void onUpdateChartData(final Snapshot snapshot, final SnapshotInfo info, final List<SnapshotChart> chartsList) {
        Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                SnapshotDao chartDataDao = mDaoSession.getSnapshotDao();
                chartDataDao.update(snapshot);

                SnapshotInfoDao infoDao = mDaoSession.getSnapshotInfoDao();
                infoDao.update(info);

                SnapshotChartDao dataChartsDao = mDaoSession.getSnapshotChartDao();
                DeleteQuery<SnapshotChart> deleteQuery = dataChartsDao.queryBuilder()
                        .where(SnapshotChartDao.Properties.SnapshotId.eq(snapshot.getId()))
                        .buildDelete();
                deleteQuery.executeDeleteWithoutDetachingEntities();

                for (int i = 0; i < chartsList.size(); i++) {
                    chartsList.get(i).setSnapshotId(snapshot.getId());
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
                SnapshotDao dataDao = mDaoSession.getSnapshotDao();
                Snapshot data = dataDao.load(key);
                data.setIsActiveForGlobal(isActive);
                data.setTiming(timing);
                dataDao.update(data);

                return dataDao.queryBuilder().where(SnapshotDao.Properties.IsActiveForGlobal.eq(true)).list().size();
            }
        });
    }

    @Override
    public Single<Integer> onDeleteChartData(final long snapshotId) {
        return Single.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                SnapshotChartDao dataChartsDao = mDaoSession.getSnapshotChartDao();
                DeleteQuery<SnapshotChart> deleteQuery = dataChartsDao.queryBuilder()
                        .where(SnapshotChartDao.Properties.SnapshotId.eq(snapshotId))
                        .buildDelete();
                deleteQuery.executeDeleteWithoutDetachingEntities();

                SnapshotInfoDao infoDao = mDaoSession.getSnapshotInfoDao();
                DeleteQuery<SnapshotInfo> deleteQuery1 = infoDao.queryBuilder()
                        .where(SnapshotInfoDao.Properties.SnapshotId.eq(snapshotId)).buildDelete();
                deleteQuery1.executeDeleteWithoutDetachingEntities();

                SnapshotDao chartDataDao = mDaoSession.getSnapshotDao();
                chartDataDao.deleteByKey(snapshotId);

                return chartDataDao.queryBuilder().where(SnapshotDao.Properties.IsActiveForGlobal.eq(true)).list().size();
            }
        });
    }
}
