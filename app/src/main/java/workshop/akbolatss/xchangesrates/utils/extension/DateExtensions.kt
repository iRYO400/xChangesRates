package workshop.akbolatss.xchangesrates.utils.extension

import android.text.format.DateUtils
import java.util.*

fun Date.getRelative(): String = DateUtils.getRelativeTimeSpanString(
    time,
    Calendar.getInstance().timeInMillis,
    DateUtils.MINUTE_IN_MILLIS
).toString()
