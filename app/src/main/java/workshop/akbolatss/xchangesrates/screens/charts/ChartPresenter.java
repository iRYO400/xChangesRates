package workshop.akbolatss.xchangesrates.screens.charts;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import workshop.akbolatss.xchangesrates.base.BasePresenter;
import workshop.akbolatss.xchangesrates.model.ExchangeModel;
import workshop.akbolatss.xchangesrates.model.mapper.ChartDataMapper;
import workshop.akbolatss.xchangesrates.model.response.ChartResponse;
import workshop.akbolatss.xchangesrates.model.response.ChartResponseData;
import workshop.akbolatss.xchangesrates.repositories.DBChartRepository;

/**
 * Author: Akbolat Sadvakassov
 * Date: 04.01.2018
 */

public class ChartPresenter extends BasePresenter<ChartView> {

    /**
     * Репозитории
     */
    private DBChartRepository mRepository;

    /**
     * Main model
     */
    private ExchangeModel mExchangeModel;

    /**
     * Response от сервера
     */
    private ChartResponseData mChartData;

    private String mCoinCode;
    private String mCurrencyCode;
    private String mTerm;

    public ChartPresenter(DBChartRepository mRepository) {
        this.mRepository = mRepository;
    }

    private CompositeDisposable compositeDisposable;

    @Override
    public void onViewAttached(ChartView view) {
        super.onViewAttached(view);
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onViewDetached(ChartView view) {
        super.onViewDetached(view);
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }

    public void onUpdate() {
        getView().onShowLoading();
        compositeDisposable.add(
                mRepository.getQueryResult(mCoinCode,
                        mExchangeModel.getIdent(),
                        mCurrencyCode,
                        mTerm)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<ChartResponse>() {
                            @Override
                            public void onSuccess(ChartResponse response) {
                                mChartData = response.getData();
                                mChartData.setCoin(mCoinCode);
                                getView().onLoadLineChart(mChartData);
                                getView().onHideLoading();
                            }

                            @Override
                            public void onError(Throwable e) {
                                getView().onHideLoading();
                            }
                        }));
    }

    public void onSaveSnap() {
        getView().onShowLoading();
        ChartDataMapper mapper = new ChartDataMapper(mChartData, mChartData.getInfo(), mChartData.getChart());
//        Flowable<Object> s = Single.concat(mapper.transformD(mChartData), //TODO Улучши, когда твои скиллы по RxJava 2 поднимутся
//                mapper.transformInfoD(mChartData.getInfo()),
//                mapper.transformChartsD(mChartData.getChart()));
        compositeDisposable.add(
                mRepository.onAddChartData(mapper.getData(), mapper.getDataInfo(), mapper.getChartsList())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Boolean>() {
                            @Override
                            public void onSuccess(Boolean aBoolean) {
                                getView().onHideLoading();
                            }

                            @Override
                            public void onError(Throwable e) {
                                getView().onHideLoading();
                            }
                        }));
    }

    public void setExchangeModel(ExchangeModel mExchangeModel) {
        this.mExchangeModel = mExchangeModel;
    }

    public ExchangeModel getExchangeModel() {
        return mExchangeModel;
    }

    public void setCoinCode(String mCoinCode) {
        this.mCoinCode = mCoinCode;
    }

    public void setCurrencyCode(String mCurrencyCode) {
        this.mCurrencyCode = mCurrencyCode;
    }

    public void setTerm(String mTerm) {
        this.mTerm = mTerm;
    }
}
