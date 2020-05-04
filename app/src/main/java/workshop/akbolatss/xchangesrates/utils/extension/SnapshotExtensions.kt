package workshop.akbolatss.xchangesrates.utils.extension

import workshop.akbolatss.xchangesrates.domain.model.Snapshot
import java.util.*

fun Snapshot.generateSnapshotName(): String {
    val locale = Locale.getDefault()
    return "${coin.toUpperCase(locale)}/${currency.toUpperCase(locale)} " +
            "@ $exchangerName"
}

fun Snapshot.generateNotificationTitle(): String {
    val locale = Locale.getDefault()
    return "${coin.toUpperCase(locale)}/${currency.toUpperCase(locale)}: " +
            "${rate.toPlainString()} @ $exchangerName"
}
