package workshop.akbolatss.xchangesrates.screens.snapshots

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import workshop.akbolatss.xchangesrates.base.BasePresenter
import workshop.akbolatss.xchangesrates.model.dao.Snapshot
import workshop.akbolatss.xchangesrates.model.mapper.ChartDataMapper
import workshop.akbolatss.xchangesrates.repositories.DBChartRepository

/**
 * Author: Akbolat Sadvakassov
 * Date: 04.01.2018
 */

class SnapshotsPresenter internal constructor(internal var mRepository: DBChartRepository) : BasePresenter<SnapshotsView>() {

    private lateinit var mCompositeDisposable: CompositeDisposable

    override fun onViewAttached(view: SnapshotsView?) {
        super.onViewAttached(view)
        mCompositeDisposable = CompositeDisposable()
    }

    override fun onViewDetached(view: SnapshotsView?) {
        super.onViewDetached(view)
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear()
        }
    }

    fun getAllSnapshots() {
        view.onShowLoading()
        mRepository.allChartData
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ chartData ->
                    if (chartData.isNotEmpty()) {
                        view.onLoadSnapshots(chartData)
                        view.onNoContent(false)
                    } else {
                        view.onNoContent(true)
                    }
                    view.onHideLoading()
                }
                        , {
                    view.onHideLoading()
                })
    }

    fun onLoadInfo(key: Long, pos: Int) {
        mCompositeDisposable.add(mRepository.getChartDataInfo(key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ dataInfo ->
                    view.onLoadInfo(dataInfo, pos)
                }, {

                }))
    }

    fun onUpdateSnapshot(snapshot: Snapshot, pos: Int) {
        mCompositeDisposable.add(mRepository.getSnapshot(
                snapshot.coin,
                snapshot.exchange,
                snapshot.currency,
                snapshot.timing
        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { chartResponse -> chartResponse.data }
                .map { responseData -> ChartDataMapper(responseData, responseData.info, responseData.chart) }
                .subscribe({ mapper ->
                    val dataInfo = mapper.dataInfo
                    dataInfo.snapshotId = snapshot.id!!
                    dataInfo.id = snapshot.infoId //TODO: getInfo().getId() or getInfoId() you should test that

                    val chartsList = mapper.chartsList
                    snapshot.charts = chartsList

                    mRepository.onUpdateChartData(snapshot, dataInfo, chartsList)

                    view.onLoadChart(snapshot, pos)
                    view.onHideLoading()
                }
                        , {
                    view.onHideLoading()
                }))

    }

    fun onUpdateOptions(chartId: Long, isActive: Boolean, timing: String) {
        mCompositeDisposable.add(mRepository.onOptionsChanged(chartId, isActive, timing)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ count ->
                    view.onSaveNotifiesCount(count!!)
                }
                        , {

                }))
    }

    fun onRemoveSnapshot(chartId: Long) {
        mCompositeDisposable.add(mRepository.onDeleteChartData(chartId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ count ->
                    view.onSaveNotifiesCount(count!!)
                }, {

                }))
    }
}
