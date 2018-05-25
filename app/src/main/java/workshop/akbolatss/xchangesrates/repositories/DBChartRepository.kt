package workshop.akbolatss.xchangesrates.repositories

import org.greenrobot.greendao.query.DeleteQuery
import java.util.concurrent.Callable

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import workshop.akbolatss.xchangesrates.model.dao.DaoSession
import workshop.akbolatss.xchangesrates.model.dao.Snapshot
import workshop.akbolatss.xchangesrates.model.dao.SnapshotChart
import workshop.akbolatss.xchangesrates.model.dao.SnapshotChartDao
import workshop.akbolatss.xchangesrates.model.dao.SnapshotDao
import workshop.akbolatss.xchangesrates.model.dao.SnapshotInfo
import workshop.akbolatss.xchangesrates.model.dao.SnapshotInfoDao
import workshop.akbolatss.xchangesrates.model.response.ChartResponse
import workshop.akbolatss.xchangesrates.networking.APIService

/**
 * Author: Akbolat Sadvakassov
 * Date: 04.01.2018
 */

class DBChartRepository(private val mDaoSession: DaoSession, private val mApiService: APIService) : ChartRepository {

    override val allChartData: Single<List<Snapshot>>
        get() = Single.fromCallable {
            val snapshotDao = mDaoSession.snapshotDao
            snapshotDao.loadAll()
        }

    override val activeChartData: Single<List<Snapshot>>
        get() = Single.fromCallable {
            val dataDao = mDaoSession.snapshotDao
            dataDao.queryBuilder().where(SnapshotDao.Properties.IsActiveForGlobal.eq(true)).list()
        }

    override fun getQueryResult(coin: String, exchange: String, currency: String, term: String): Single<ChartResponse> {
        return mApiService.getCurrency(coin, exchange, currency, term)
    }

    override fun getChartDataInfo(key: Long): Single<SnapshotInfo> {
        return Single.fromCallable {
            val infoDao = mDaoSession.snapshotInfoDao
            infoDao.load(key)
        }
    }

    override fun getSnapshot(coin: String, exchange: String, currency: String, term: String): Single<ChartResponse> {
        return mApiService.getSnapshot(coin, exchange, currency, term)
    }

    override fun onAddChartData(snapshot: Snapshot, info: SnapshotInfo, chartsList: List<SnapshotChart>): Single<Boolean> {
        return Single.fromCallable {
            val chartDataDao = mDaoSession.snapshotDao
            val dataId = chartDataDao.insert(snapshot)

            val infoDao = mDaoSession.snapshotInfoDao
            info.snapshotId = dataId
            infoDao.insert(info)

            val dataChartsDao = mDaoSession.snapshotChartDao
            for (i in chartsList.indices) {
                chartsList[i].snapshotId = dataId
                dataChartsDao.insert(chartsList[i])
            }

            //                Snapshot newData = chartDataDao.load(dataId);
            //
            //                try {
            //                    Log.d("TAG", "NewData " + newData.getInfo().getInfoId() + " newDataId " + newData.getId());
            //                } catch (NullPointerException e) {
            //                    e.printStackTrace();
            //                }
            true
        }
    }

    override fun onUpdateChartData(snapshot: Snapshot, info: SnapshotInfo, chartsList: List<SnapshotChart>) {
        Single.fromCallable {
            val chartDataDao = mDaoSession.snapshotDao
            chartDataDao.update(snapshot)

            val infoDao = mDaoSession.snapshotInfoDao
            infoDao.update(info)

            val dataChartsDao = mDaoSession.snapshotChartDao
            val deleteQuery = dataChartsDao.queryBuilder()
                    .where(SnapshotChartDao.Properties.SnapshotId.eq(snapshot.id))
                    .buildDelete()
            deleteQuery.executeDeleteWithoutDetachingEntities()

            for (i in chartsList.indices) {
                chartsList[i].snapshotId = snapshot.id!!
                dataChartsDao.insert(chartsList[i])
            }
            true
        }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    override fun onOptionsChanged(key: Long, isActive: Boolean, timing: String): Single<Int> {
        return Single.fromCallable {
            val dataDao = mDaoSession.snapshotDao
            val data = dataDao.load(key)
            data.isActiveForGlobal = isActive
            data.timing = timing
            dataDao.update(data)

            dataDao.queryBuilder().where(SnapshotDao.Properties.IsActiveForGlobal.eq(true)).list().size
        }
    }

    override fun onDeleteChartData(snapshotId: Long): Single<Int> {
        return Single.fromCallable {
            val dataChartsDao = mDaoSession.snapshotChartDao
            val deleteQuery = dataChartsDao.queryBuilder()
                    .where(SnapshotChartDao.Properties.SnapshotId.eq(snapshotId))
                    .buildDelete()
            deleteQuery.executeDeleteWithoutDetachingEntities()

            val infoDao = mDaoSession.snapshotInfoDao
            val deleteQuery1 = infoDao.queryBuilder()
                    .where(SnapshotInfoDao.Properties.SnapshotId.eq(snapshotId)).buildDelete()
            deleteQuery1.executeDeleteWithoutDetachingEntities()

            val chartDataDao = mDaoSession.snapshotDao
            chartDataDao.deleteByKey(snapshotId)

            chartDataDao.queryBuilder().where(SnapshotDao.Properties.IsActiveForGlobal.eq(true)).list().size
        }
    }
}
