package workshop.akbolatss.xchangesrates.screens.snapshots

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import workshop.akbolatss.xchangesrates.base.BasePresenter
import workshop.akbolatss.xchangesrates.model.response.ChartData
import workshop.akbolatss.xchangesrates.model.response.ChartResponse
import workshop.akbolatss.xchangesrates.repositories.DBChartRepository
import workshop.akbolatss.xchangesrates.utils.Logger


class SnapshotsPresenter internal constructor(private var mRepository: DBChartRepository) : BasePresenter<SnapshotsView>() {

    private lateinit var mCompositeDisposable: CompositeDisposable

    override fun onViewAttached(view: SnapshotsView?) {
        super.onViewAttached(view)
        mCompositeDisposable = CompositeDisposable()
    }

    override fun onViewDetached(view: SnapshotsView?) {
        super.onViewDetached(view)
        if (::mCompositeDisposable.isInitialized) {
            mCompositeDisposable.clear()
        }
    }

    fun getAllSnapshots() {
        view.onShowLoading()
        mCompositeDisposable.add(mRepository.allChartData
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ chartData ->
                    if (chartData.isNotEmpty()) {
                        view.loadChartDatas(chartData)
                        view.onNoContent(false)
                    } else {
                        view.onNoContent(true)
                    }
                    view.onHideLoading()
                }
                        , {
                    it.printStackTrace()
                    view.onError(it.message!!)
                    view.onHideLoading()
                })
        )
    }

    fun onUpdateSnapshot(chartData: ChartData, pos: Int) {
        mCompositeDisposable.add(mRepository.getSnapshot(
                chartData.coin,
                chartData.exchange,
                chartData.currency,
                chartData.timingName)
                .map { chartResponse ->
                    chartResponse.data
                }
                .map { newChartData ->
                    newChartData.id = chartData.id
                    newChartData.coin = chartData.coin
                    newChartData.timingName = chartData.timingName
                    newChartData.timingIndex = chartData.timingIndex
                    newChartData.options = chartData.options
                    newChartData.isNotificationEnabled = chartData.isNotificationEnabled
                    newChartData
                }
                .flatMap { newChartData ->
                    mRepository.onUpdateChartData(newChartData)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ newChartData ->
                    view.loadChartData(newChartData, pos)
                }
                        , {
                    it.printStackTrace()
                    view.onError(it.message!!)
                    view.onErrorChartItem(pos)
                })
        )

    }

    fun onUpdateOptions(chartData: ChartData, pos: Int) {
        mCompositeDisposable.add(mRepository.onUpdateChartData(chartData)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Logger.i("Updated Snapshot")
                    view.loadChartData(it, pos)

//                    checkForService()
                }
                        , {
                    it.printStackTrace()
                    Logger.e("Updating failed ${it.message}")
                }))
    }

    fun onRemoveSnapshot(chartId: Long) {
        mCompositeDisposable.add(mRepository.onDeleteChartData(chartId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Logger.i("Successfully Removed")
//                    checkForService()
                }, {
                    it.printStackTrace()
                    Logger.e("Removing failed ${it.message}")
                }))
    }

    fun onUpdateAllSnapshots(list: MutableList<ChartData>) {
        view.onShowLoading()
        mCompositeDisposable.add(Observable.fromIterable(list)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .flatMap { oldChartData ->
                    Observable.zip(Observable.just(oldChartData),
                            mRepository.getSnapshot(
                                    oldChartData.coin,
                                    oldChartData.exchange,
                                    oldChartData.currency,
                                    oldChartData.timingName)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread()),
                            BiFunction<ChartData, ChartResponse, ChartData> { chartData, chartResponse ->
                                val newChartData: ChartData = chartResponse.data
                                newChartData.id = chartData.id
                                newChartData.coin = chartData.coin
                                newChartData.timingName = chartData.timingName
                                newChartData.timingIndex = chartData.timingIndex
                                newChartData.isNotificationEnabled = chartData.isNotificationEnabled
                                newChartData.options = chartData.options
                                newChartData
                            }
                    )
                }
                .doOnNext { newChartData ->
                    mRepository.onUpdateChartData(newChartData)
                }
                .toList()
                .subscribe({
                    view.loadChartDatas(it)
                    view.onHideLoading()
                }, {
                    getAllSnapshots()
                    it.printStackTrace()
                    view.onError(it.localizedMessage)
                    view.onHideLoading()
                })
        )
    }
}
