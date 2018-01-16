package workshop.akbolatss.xchangesrates.screens.snapshots;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import workshop.akbolatss.xchangesrates.base.BasePresenter;
import workshop.akbolatss.xchangesrates.model.dao.ChartData;
import workshop.akbolatss.xchangesrates.model.dao.ChartDataCharts;
import workshop.akbolatss.xchangesrates.model.dao.ChartDataInfo;
import workshop.akbolatss.xchangesrates.model.mapper.ChartDataMapper;
import workshop.akbolatss.xchangesrates.model.response.ChartResponse;
import workshop.akbolatss.xchangesrates.model.response.ChartResponseData;
import workshop.akbolatss.xchangesrates.repositories.DBChartRepository;

import static workshop.akbolatss.xchangesrates.utils.Constants.HOUR_24;

/**
 * Author: Akbolat Sadvakassov
 * Date: 04.01.2018
 */

public class SnapshotsPresenter extends BasePresenter<SnapshotsView> {

    private DBChartRepository mRepository;

    @NonNull
    private CompositeDisposable mCompositeDisposable;

    public SnapshotsPresenter(DBChartRepository mRepository) {
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

    public void onGetSnapshots() {
        getView().onShowLoading();
        mRepository.getAllChartData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<List<ChartData>>() {
                    @Override
                    public void onSuccess(List<ChartData> chartData) {
                        getView().onHideLoading();
                        if (chartData.size() > 0) {
                            getView().onLoadCharts(chartData);
                            getView().onNoContent(false);
                        } else {
                            getView().onNoContent(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().onHideLoading();
                    }
                });
    }

    public void onLoadInfo(long key, final int pos){
        mRepository.getChartDataInfo(key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<ChartDataInfo>() {
                    @Override
                    public void onSuccess(ChartDataInfo dataInfo) {
                        getView().onLoadChartInfo(dataInfo, pos);
                    }

                    @Override
                    public void onError(Throwable e) {}
                });
    }

    public void onUpdateSnapshot(final ChartData data, final int pos) {
        mCompositeDisposable.add(mRepository.getSnapshot(
                data.getCoin(),
                data.getExchange(),
                data.getCurrency(),
                HOUR_24
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
                        ChartDataInfo dataInfo = mapper.getDataInfo();
                        dataInfo.setInfoId(data.getInfo().getInfoId());
                        dataInfo.setId(data.getInfo().getId());
                        data.setInfo(dataInfo);
                        List<ChartDataCharts> chartsList = mapper.getChartsList();
                        mRepository.onUpdateChartData(data, dataInfo, chartsList);

                        getView().onLoadChart(data, pos);
                        getView().onHideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().onHideLoading();
                    }
                }));
    }
}
