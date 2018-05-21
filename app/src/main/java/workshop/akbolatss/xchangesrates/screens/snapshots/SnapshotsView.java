package workshop.akbolatss.xchangesrates.screens.snapshots;

import java.util.List;

import workshop.akbolatss.xchangesrates.base.BaseView;
import workshop.akbolatss.xchangesrates.base.LoadingView;
import workshop.akbolatss.xchangesrates.model.dao.Snapshot;
import workshop.akbolatss.xchangesrates.model.dao.SnapshotInfo;

/**
 * Author: Akbolat Sadvakassov
 * Date: 04.01.2018
 */

public interface SnapshotsView extends BaseView, LoadingView {

    public void onLoadSnapshots(List<Snapshot> snapshotList);

    public void onLoadInfo(SnapshotInfo dataInfo, int pos);

    public void onLoadChart(Snapshot data, int pos);

    public void onSaveNotifiesCount(int count);
}
