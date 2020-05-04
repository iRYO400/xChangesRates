package workshop.akbolatss.xchangesrates.utils.extension

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*


const val TIMESTAMP_STRING_FORMAT = "dd-MM-yyyy - HH:mm"

fun Date.getRelative(): String = DateUtils.getRelativeTimeSpanString(
    time,
    Calendar.getInstance().timeInMillis,
    DateUtils.MINUTE_IN_MILLIS
).toString()

fun Date.toFull(): String =
    SimpleDateFormat(TIMESTAMP_STRING_FORMAT, Locale.getDefault()).format(this)
