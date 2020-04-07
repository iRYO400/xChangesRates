package workshop.akbolatss.xchangesrates.presentation.root

import me.yokeyword.fragmentation.ISupportFragment
import workshop.akbolatss.xchangesrates.presentation.chart.ChartFragment
import workshop.akbolatss.xchangesrates.screens.snapshots.SnapshotsFragment

enum class ScreenState(
        val fragment: Class<out ISupportFragment>
) {
    CHART(ChartFragment::class.java),
    SNAPSHOTS(SnapshotsFragment::class.java)
}
