package workshop.akbolatss.xchangesrates.model.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import workshop.akbolatss.xchangesrates.model.dao.ChartData;
import workshop.akbolatss.xchangesrates.model.dao.ChartDataCharts;
import workshop.akbolatss.xchangesrates.model.dao.ChartDataInfo;
import workshop.akbolatss.xchangesrates.model.response.ChartResponse;
import workshop.akbolatss.xchangesrates.model.response.ChartResponseData;
import workshop.akbolatss.xchangesrates.model.response.ChartResponseDataChart;
import workshop.akbolatss.xchangesrates.model.response.ChartResponseDataInfo;

/**
 * Author: Akbolat Sadvakassov
 * Date: 14.01.2018
 */

public class ChartDataMapper {

    private ChartData data;
    private ChartDataInfo dataInfo;
    private List<ChartDataCharts> chartsList;

    public ChartDataMapper(ChartResponseData data, ChartResponseDataInfo dataInfo, List<ChartResponseDataChart> chartsList) {
        this.data = transformData(data);
        this.dataInfo = transformInfo(dataInfo);
        this.chartsList = transformCharts(chartsList);
    }

    public ChartData getData() {
        return data;
    }

    public ChartDataInfo getDataInfo() {
        return dataInfo;
    }

    public List<ChartDataCharts> getChartsList() {
        return chartsList;
    }

    public ChartData transformData(ChartResponseData responseData) {
        ChartData chartData = null;
        if (responseData != null) {
            chartData = new ChartData();
            chartData.setCoin(responseData.getCoin());
            chartData.setCurrency(responseData.getCurrency());
            chartData.setExchange(responseData.getExchange());
            chartData.setSource(responseData.getSource());
        }
        return chartData;
    }

    public Single<ChartData> transformD(final ChartResponseData responseData){
        return Single.fromCallable(new Callable<ChartData>() {
            @Override
            public ChartData call() throws Exception {
                ChartData chartData = null;
                if (responseData != null) {
                    chartData = new ChartData();
                    chartData.setCoin(responseData.getCoin());
                    chartData.setCurrency(responseData.getCurrency());
                    chartData.setExchange(responseData.getExchange());
                    chartData.setSource(responseData.getSource());
                }
                return chartData;
            }
        });
    }

    public Single<ChartDataInfo> transformInfoD(final ChartResponseDataInfo dataInfo){
        return Single.fromCallable(new Callable<ChartDataInfo>() {
            @Override
            public ChartDataInfo call() throws Exception {
                ChartDataInfo info = null;
                if (dataInfo != null) {
                    info = new ChartDataInfo();
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
        });
    }

    public ChartDataInfo transformInfo(ChartResponseDataInfo dataInfo) {
        ChartDataInfo info = null;
        if (dataInfo != null) {
            info = new ChartDataInfo();
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

    public Single<List<ChartDataCharts>> transformChartsD(final List<ChartResponseDataChart> list){
        return Single.fromCallable(new Callable<List<ChartDataCharts>>() {
            @Override
            public List<ChartDataCharts> call() throws Exception {
                List<ChartDataCharts> chartsList = null;
                if (list != null) {
                    chartsList = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        ChartDataCharts dataCharts = new ChartDataCharts();
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
        });
    }

    public List<ChartDataCharts> transformCharts(List<ChartResponseDataChart> list){
        List<ChartDataCharts> chartsList = null;
        if (list != null) {
            chartsList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                ChartDataCharts dataCharts = new ChartDataCharts();
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
}
