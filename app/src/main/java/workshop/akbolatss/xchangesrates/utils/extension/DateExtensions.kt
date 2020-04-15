package workshop.akbolatss.xchangesrates.utils.extension

import android.text.format.DateUtils
import android.util.Log
import java.text.ParseException
import java.util.*

fun convertTime(timestamp: Long): String {
    try {
        val niceDateStr = DateUtils.getRelativeTimeSpanString(
            timestamp,
            Calendar.getInstance().timeInMillis,
            DateUtils.MINUTE_IN_MILLIS
        )
        return niceDateStr.toString()
    } catch (e: ParseException) {
        Log.e("ParseException", "Unparseable date " + e.message)
        return timestamp.toString()
    }
}

fun Date.getRelative(): String = DateUtils.getRelativeTimeSpanString(
    time,
    Calendar.getInstance().timeInMillis,
    DateUtils.MINUTE_IN_MILLIS
).toString()
