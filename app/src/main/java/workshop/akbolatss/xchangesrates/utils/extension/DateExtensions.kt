package workshop.akbolatss.xchangesrates.utils.extension

import android.text.format.DateUtils
import android.util.Log
import java.text.ParseException
import java.util.*

fun Date.getRelative(): String = DateUtils.getRelativeTimeSpanString(
    time,
    Calendar.getInstance().timeInMillis,
    DateUtils.MINUTE_IN_MILLIS
).toString()
