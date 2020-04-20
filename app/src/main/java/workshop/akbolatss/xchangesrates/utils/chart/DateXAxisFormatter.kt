package workshop.akbolatss.xchangesrates.utils.chart


import com.github.mikephil.charting.formatter.ValueFormatter
import workshop.akbolatss.xchangesrates.domain.model.PriceByTime
import workshop.akbolatss.xchangesrates.utils.extension.empty
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class DateXAxisFormatter(
    private val units: List<PriceByTime>
) : ValueFormatter() {

    private val formatter: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    override fun getFormattedValue(value: Float): String {
        val index = value.roundToInt()

        if (index < 0 || index >= units.size || index != value.toInt())
            return String.empty()

        val timestamp = units[index].timestamp * 1000
        return getDate(timestamp)
    }

    private fun getDate(timeStamp: Long): String {
        return formatter.format(Date(timeStamp))
    }
}
