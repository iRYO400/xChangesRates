package workshop.akbolatss.xchangesrates.domain.model

import workshop.akbolatss.xchangesrates.utils.extension.defaultVal

data class SnapshotOptions(
    val id: Long,
    val snapshotId: Long,
    val isNotificationEnabled: Boolean,
    val isSmartEnabled: Boolean,
    val isStick: Boolean,
    val updateInterval: UpdateInterval,
    val changesForPeriod: ChangesForPeriod
) {
    companion object {
        fun empty() = SnapshotOptions(
            id = defaultVal(),
            snapshotId = defaultVal(),
            isNotificationEnabled = false,
            isSmartEnabled = false,
            isStick = false,
            updateInterval = UpdateInterval.HOUR_1,
            changesForPeriod = ChangesForPeriod.HOUR_24
        )
    }
}

enum class ChangesForPeriod(val queryParam: String) {
    MINUTES_10("10min"),
    HOUR_1("1h"),
    HOUR_3("3h"),
    HOUR_12("12h"),
    HOUR_24("24h"),
    WEEK("1w"),
    MONTH("1m"),
    MONTH_3("3m"),
    MONTH_6("6m"),
    YEAR_1("1y"),
    YEAR_2("2y"),
    YEAR_5("5y"),
}

enum class UpdateInterval(val seconds: Long) {
    MIN_15(15 * 60),
    MIN_30(30 * 60),
    HOUR_1(60 * 60),
    HOUR_2(2 * 60 * 60),
    HOUR_5(5 * 60 * 60),
    HOUR_12(12 * 60 * 60),
    HOUR_24(24 * 60 * 60),
    HOUR_48(48 * 60 * 60),
    WEEK_1(168 * 60 * 60),
    WEEK_2(336 * 60 * 60),
}
