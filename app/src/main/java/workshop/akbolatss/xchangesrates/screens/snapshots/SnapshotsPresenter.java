package workshop.akbolatss.xchangesrates.screens.snapshots;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import workshop.akbolatss.xchangesrates.base.BasePresenter;
import workshop.akbolatss.xchangesrates.model.dao.Snapshot;
import workshop.akbolatss.xchangesrates.model.dao.SnapshotChart;
import workshop.akbolatss.xchangesrates.model.dao.SnapshotInfo;
import workshop.akbolatss.xchangesrates.model.mapper.ChartDataMapper;
import workshop.akbolatss.xchangesrates.model.response.ChartResponse;
import workshop.akbolatss.xchangesrates.model.response.ChartResponseData;
import workshop.akbolatss.xchangesrates.repositories.DBChartRepository;

/**
 * Author: Akbolat Sadvakassov
 * Date: 04.01.2018
 */

public class SnapshotsPresenter extends BasePresenter<SnapshotsView> {

    DBChartRepository mRepository;

    @NonNull
    private CompositeDisposable mCompositeDisposable;

    SnapshotsPresenter(DBChartRepository mRepository) {
        this.mRepository = mRepository;
    }

    @Override
    public void onViewAttached(SnapshotsView view) {
        super.onViewAttached(view);
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onViewDetached(SnapshotsView view) {
        super.onViewDetached(view);
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
    }

    public void getAllSnapshots() {
        getView().onShowLoading();
        mRepository.getAllChartData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<List<Snapshot>>() {
                    @Override
                    public void onSuccess(List<Snapshot> chartData) {
                        if (chartData.size() > 0) {
                            getView().onLoadSnapshots(chartData);
                            getView().onNoContent(false);
                        } else {
                            getView().onNoContent(true);
                        }
                        getView().onHideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().onHideLoading();
                    }
                });
    }

    public void onLoadInfo(long key, final int pos) {
        mCompositeDisposable.add(mRepository.getChartDataInfo(key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<SnapshotInfo>() {
                    @Override
                    public void onSuccess(SnapshotInfo dataInfo) {
                        getView().onLoadInfo(dataInfo, pos);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                }));
    }

    public void onUpdateSnapshot(final Snapshot snapshot, final int pos) {
        mCompositeDisposable.add(mRepository.getSnapshot(
                snapshot.getCoin(),
                snapshot.getExchange(),
                snapshot.getCurrency(),
                snapshot.getTiming()
        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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
                .subscribeWith(new DisposableSingleObserver<ChartDataMapper>() {
                    @Override
                    public void onSuccess(ChartDataMapper mapper) {
                        SnapshotInfo dataInfo = mapper.getDataInfo();
                        dataInfo.setSnapshotId(snapshot.getId());
                        dataInfo.setId(snapshot.getInfoId()); //TODO: getInfo().getId() or getInfoId() you should test that

                        List<SnapshotChart> chartsList = mapper.getChartsList();
                        snapshot.setCharts(chartsList);

                        mRepository.onUpdateChartData(snapshot, dataInfo, chartsList);

                        getView().onLoadChart(snapshot, pos);
                        getView().onHideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().onHideLoading();
                    }
                }));
    }

    public void onUpdateOptions(long chartId, boolean isActive, String timing) {
        mCompositeDisposable.add(mRepository.onOptionsChanged(chartId, isActive, timing)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<Integer>() {
                    @Override
                    public void onSuccess(Integer count) {
                        getView().onSaveNotifiesCount(count);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }));
    }

    public void onRemoveSnapshot(long chartId) {
        mCompositeDisposable.add(mRepository.onDeleteChartData(chartId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<Integer>() {
                    @Override
                    public void onSuccess(Integer count) {
                        getView().onSaveNotifiesCount(count);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }));
    }
}
