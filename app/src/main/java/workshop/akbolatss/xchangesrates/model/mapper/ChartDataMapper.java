package workshop.akbolatss.xchangesrates.model.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import workshop.akbolatss.xchangesrates.model.dao.Snapshot;
import workshop.akbolatss.xchangesrates.model.dao.SnapshotChart;
import workshop.akbolatss.xchangesrates.model.dao.SnapshotInfo;
import workshop.akbolatss.xchangesrates.model.response.ChartResponseData;
import workshop.akbolatss.xchangesrates.model.response.ChartResponseDataChart;
import workshop.akbolatss.xchangesrates.model.response.ChartResponseDataInfo;

import static workshop.akbolatss.xchangesrates.utils.Constants.HOUR_24;

/**
 * Author: Akbolat Sadvakassov
 * Date: 14.01.2018
 */

public class ChartDataMapper {

    private Snapshot data;
    private SnapshotInfo dataInfo;
    private List<SnapshotChart> chartsList;

    public ChartDataMapper(ChartResponseData data, ChartResponseDataInfo dataInfo, List<ChartResponseDataChart> chartsList) {
        this.data = transformData(data);
        this.dataInfo = transformInfo(dataInfo);
        this.chartsList = transformCharts(chartsList);
    }

    public Snapshot getData() {
        return data;
    }

    public SnapshotInfo getDataInfo() {
        return dataInfo;
    }

    public List<SnapshotChart> getChartsList() {
        return chartsList;
    }

    public Snapshot transformData(ChartResponseData responseData) {
        Snapshot snapshot = null;
        if (responseData != null) {
            snapshot = new Snapshot();
            snapshot.setCoin(responseData.getCoin());
            snapshot.setCurrency(responseData.getCurrency());
            snapshot.setExchange(responseData.getExchange());
            snapshot.setSource(responseData.getSource());
            snapshot.setIsActiveForGlobal(false);
            snapshot.setTiming(HOUR_24);
            snapshot.setIsIntervalEnabled(false);
            snapshot.setIntervalNumber(10);
            snapshot.setIntervalType(2);
            snapshot.setIsNotifyPersistent(false);
        }
        return snapshot;
    }

    public SnapshotInfo transformInfo(ChartResponseDataInfo dataInfo) {
        SnapshotInfo info = null;
        if (dataInfo != null) {
            info = new SnapshotInfo();
            info.setBuy(dataInfo.getBuy());
            info.setChange(dataInfo.getChange());
            info.setChange24(dataInfo.getChange24());
            info.setHigh(dataInfo.getHigh());
            info.setLast(dataInfo.getLast());
            info.setLow(dataInfo.getLow());
            info.setMultiply(dataInfo.getMultiply());
            info.setSell(dataInfo.getSell());
            info.setStarted(dataInfo.getStarted());
            info.setTimestamp(dataInfo.getTimestamp());
            info.setUpdated(dataInfo.getUpdated());
            info.setVolume(dataInfo.getVolume());
        }
        return info;
    }

    public List<SnapshotChart> transformCharts(List<ChartResponseDataChart> list) {
        List<SnapshotChart> chartsList = null;
        if (list != null) {
            chartsList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                SnapshotChart dataCharts = new SnapshotChart();
                dataCharts.setHigh(list.get(i).getHigh());
                dataCharts.setLow(list.get(i).getLow());
                dataCharts.setMarket(list.get(i).getMarket());
                dataCharts.setPrice(list.get(i).getPrice());
                dataCharts.setTimestamp(list.get(i).getTimestamp());
                chartsList.add(dataCharts);
            }
        }
        return chartsList;
    }

    //    public Single<Snapshot> transformD(final ChartResponseData responseData) {
//        return Single.fromCallable(new Callable<Snapshot>() {
//            @Override
//            public Snapshot call() throws Exception {
//                Snapshot snapshot = null;
//                if (responseData != null) {
//                    snapshot = new Snapshot();
//                    snapshot.setCoin(responseData.getCoin());
//                    snapshot.setCurrency(responseData.getCurrency());
//                    snapshot.setExchange(responseData.getExchange());
//                    snapshot.setSource(responseData.getSource());
//                }
//                return snapshot;
//            }
//        });
//    }
//
//    public Single<SnapshotInfo> transformInfoD(final ChartResponseDataInfo dataInfo) {
//        return Single.fromCallable(new Callable<SnapshotInfo>() {
//            @Override
//            public SnapshotInfo call() throws Exception {
//                SnapshotInfo info = null;
//                if (dataInfo != null) {
//                    info = new SnapshotInfo();
//                    info.setBuy(dataInfo.getBuy());
//                    info.setChange(dataInfo.getChange());
//                    info.setChange24(dataInfo.getChange24());
//                    info.setHigh(dataInfo.getHigh());
//                    info.setLast(dataInfo.getLast());
//                    info.setLow(dataInfo.getLow());
//                    info.setMultiply(dataInfo.getMultiply());
//                    info.setSell(dataInfo.getSell());
//                    info.setStarted(dataInfo.getStarted());
//                    info.setTimestamp(dataInfo.getTimestamp());
//                    info.setUpdated(dataInfo.getUpdated());
//                    info.setVolume(dataInfo.getVolume());
//                }
//                return info;
//            }
//        });
//    }
//
//    public Single<List<SnapshotChart>> transformChartsD(final List<ChartResponseDataChart> list) {
//        return Single.fromCallable(new Callable<List<SnapshotChart>>() {
//            @Override
//            public List<SnapshotChart> call() throws Exception {
//                List<SnapshotChart> chartsList = null;
//                if (list != null) {
//                    chartsList = new ArrayList<>();
//                    for (int i = 0; i < list.size(); i++) {
//                        SnapshotChart dataCharts = new SnapshotChart();
//                        dataCharts.setHigh(list.get(i).getHigh());
//                        dataCharts.setLow(list.get(i).getLow());
//                        dataCharts.setMarket(list.get(i).getMarket());
//                        dataCharts.setPrice(list.get(i).getPrice());
//                        dataCharts.setTimestamp(list.get(i).getTimestamp());
//                        chartsList.add(dataCharts);
//                    }
//                }
//                return chartsList;
//            }
//        });
//    }
}
