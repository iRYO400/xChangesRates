package workshop.akbolatss.xchangesrates.utils


import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Author: Akbolat Sadvakassov
 * Date: 21.11.2017
 */

class DateXValueFormatter : IAxisValueFormatter {

    private val mDataFormat: DateFormat
    private val mDate: Date

    init {
        this.mDataFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH)
        this.mDate = Date()
    }

    override fun getFormattedValue(value: Float, axis: AxisBase): String {
        val convertedTimestamp = value.toLong() * 1000
        return getDate(convertedTimestamp)
    }

    private fun getDate(timeStamp: Long): String {
        try {
            mDate.time = timeStamp
            return mDataFormat.format(mDate)
        } catch (ex: Exception) {
            return "xx"
        }

    }
}
