package workshop.akbolatss.xchangesrates.repositories;

import java.util.List;

import io.reactivex.Single;
import workshop.akbolatss.xchangesrates.model.dao.ChartData;
import workshop.akbolatss.xchangesrates.model.dao.ChartDataCharts;
import workshop.akbolatss.xchangesrates.model.dao.ChartDataInfo;
import workshop.akbolatss.xchangesrates.model.response.ChartResponse;
import workshop.akbolatss.xchangesrates.model.response.ChartResponseData;

/**
 * Author: Akbolat Sadvakassov
 * Date: 04.01.2018
 */

public interface ChartRepository {

    Single<ChartResponse> getQueryResult(String coin, String exchange,
                                         String currency, String term);

    Single<List<ChartData>> getAllChartData();

    Single<List<ChartData>> getActiveChartData();

    Single<ChartDataInfo> getChartDataInfo(long key);

    Single<ChartResponse> getSnapshot(String coin, String exchange,
                                  String currency, String term);

    Single<Boolean> onAddChartData(ChartData data, ChartDataInfo dataInfo, List<ChartDataCharts> dataCharts);

    Single<Integer> onDeleteChartData(long key);

    void onUpdateChartData(ChartData chartData, ChartDataInfo dataInfo, List<ChartDataCharts> chartsList);

    Single<Integer> onOptionsChanged(long key, boolean isActive, String timing);
}
