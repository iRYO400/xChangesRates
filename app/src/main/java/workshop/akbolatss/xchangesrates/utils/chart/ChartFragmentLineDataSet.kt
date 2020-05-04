package workshop.akbolatss.xchangesrates.utils.chart

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.utils.extension.getThemeColor

class ChartFragmentLineDataSet(
    entries: List<Entry>, label: String?, context: Context
) : LineDataSet(entries, label) {

    init {
        setDrawCircles(true)
        mode = Mode.CUBIC_BEZIER
        cubicIntensity = 0.2f
        lineWidth = 1.8f
        color = context.getThemeColor(R.attr.colorAccent)
        circleRadius = 1.4f
        setCircleColor(Color.WHITE)
        valueTextColor = Color.WHITE

        setDrawFilled(true)
        fillDrawable =
            ContextCompat.getDrawable(context, R.drawable.bg_chart_fill_in_chart_fragment)
    }
}
