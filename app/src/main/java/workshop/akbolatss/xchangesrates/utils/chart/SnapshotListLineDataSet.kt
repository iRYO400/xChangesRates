package workshop.akbolatss.xchangesrates.utils.chart

import android.content.Context
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.utils.extension.getThemeColor

class SnapshotListLineDataSet(
    entries: List<Entry>, label: String?, context: Context
) : LineDataSet(entries, label) {

    init {
        setDrawCircles(false)
        mode = Mode.CUBIC_BEZIER
        cubicIntensity = 0.4f
        lineWidth = 1f
        color = context.getThemeColor(R.attr.colorAccent)

        setDrawFilled(true)
        fillDrawable =
            ContextCompat.getDrawable(context, R.drawable.bg_chart_fill_in_snapshot_list)
    }
}
