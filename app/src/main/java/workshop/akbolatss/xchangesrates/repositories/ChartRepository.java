package workshop.akbolatss.xchangesrates.repositories;

import java.util.List;

import io.reactivex.Single;
import workshop.akbolatss.xchangesrates.model.dao.Snapshot;
import workshop.akbolatss.xchangesrates.model.dao.SnapshotChart;
import workshop.akbolatss.xchangesrates.model.dao.SnapshotInfo;
import workshop.akbolatss.xchangesrates.model.response.ChartResponse;

/**
 * Author: Akbolat Sadvakassov
 * Date: 04.01.2018
 */

public interface ChartRepository {

    Single<ChartResponse> getQueryResult(String coin, String exchange,
                                         String currency, String term);

    Single<List<Snapshot>> getAllChartData();

    Single<List<Snapshot>> getActiveChartData();

    Single<SnapshotInfo> getChartDataInfo(long key);

    Single<ChartResponse> getSnapshot(String coin, String exchange,
                                      String currency, String term);

    Single<Boolean> onAddChartData(Snapshot data, SnapshotInfo dataInfo, List<SnapshotChart> dataCharts);

    Single<Integer> onDeleteChartData(long key);

    void onUpdateChartData(Snapshot snapshot, SnapshotInfo dataInfo, List<SnapshotChart> chartsList);

    Single<Integer> onOptionsChanged(long key, boolean isActive, String timing);
}
