package workshop.akbolatss.xchangesrates.screens.snapshots

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import workshop.akbolatss.xchangesrates.base.BasePresenter
import workshop.akbolatss.xchangesrates.data.repository.ChartRepositoryImpl
import workshop.akbolatss.xchangesrates.model.response.ChartData
import workshop.akbolatss.xchangesrates.utils.Logger


class SnapshotsPresenter internal constructor(
        private var mRepository: ChartRepositoryImpl
) : BasePresenter<SnapshotsView>() {

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

    fun onUpdateOptions(chartData: ChartData, pos: Int) {
        mCompositeDisposable.add(mRepository.onUpdateChartData(chartData)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Logger.i("Updated Snapshot")
                    view.loadChartData(it, pos)

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

}
