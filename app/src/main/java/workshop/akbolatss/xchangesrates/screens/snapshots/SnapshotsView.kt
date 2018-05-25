package workshop.akbolatss.xchangesrates.screens.snapshots

import workshop.akbolatss.xchangesrates.base.BaseView
import workshop.akbolatss.xchangesrates.base.LoadingView
import workshop.akbolatss.xchangesrates.model.dao.Snapshot
import workshop.akbolatss.xchangesrates.model.dao.SnapshotInfo

/**
 * Author: Akbolat Sadvakassov
 * Date: 04.01.2018
 */

interface SnapshotsView : BaseView, LoadingView {

    fun onLoadSnapshots(snapshotList: List<Snapshot>)

    fun onLoadInfo(dataInfo: SnapshotInfo, pos: Int)

    fun onLoadChart(data: Snapshot, pos: Int)

    fun onSaveNotifiesCount(count: Int)
}
