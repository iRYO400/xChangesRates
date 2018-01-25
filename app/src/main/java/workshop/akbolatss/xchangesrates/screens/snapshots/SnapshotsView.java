package workshop.akbolatss.xchangesrates.screens.snapshots;

import java.util.List;

import workshop.akbolatss.xchangesrates.base.BaseView;
import workshop.akbolatss.xchangesrates.base.LoadingView;
import workshop.akbolatss.xchangesrates.model.dao.ChartData;
import workshop.akbolatss.xchangesrates.model.dao.ChartDataInfo;

/**
 * Author: Akbolat Sadvakassov
 * Date: 04.01.2018
 */

public interface SnapshotsView extends BaseView, LoadingView {

    public void getSnapshots(List<ChartData> chartDataList);

    public void onLoadChartInfo(ChartDataInfo dataInfo, int pos);

    public void onLoadChart(ChartData data, int pos);

    public void onSaveNotifiesCount(int count);
}
