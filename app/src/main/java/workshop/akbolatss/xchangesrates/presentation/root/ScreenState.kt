package workshop.akbolatss.xchangesrates.presentation.root

import androidx.annotation.DrawableRes
import me.yokeyword.fragmentation.ISupportFragment
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.presentation.chart.ChartFragment
import workshop.akbolatss.xchangesrates.presentation.snapshots.SnapshotListFragment

enum class ScreenState(
    val fragment: Class<out ISupportFragment>,
    @DrawableRes val buttonIcon: Int
) {
    CHART(ChartFragment::class.java, R.drawable.ic_round_add),
    SNAPSHOTS(SnapshotListFragment::class.java, R.drawable.ic_round_refresh)
}
