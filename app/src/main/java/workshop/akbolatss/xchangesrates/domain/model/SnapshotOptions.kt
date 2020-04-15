package workshop.akbolatss.xchangesrates.domain.model

import workshop.akbolatss.xchangesrates.utils.extension.defaultVal

data class SnapshotOptions(
    val id: Long,
    val isSmartEnabled: Boolean,
    val isStick: Boolean,
    val updateInterval: UpdateInterval,
    val changesForPeriod: ChangesForPeriod
) {
    companion object {
        fun empty() = SnapshotOptions(
            id = defaultVal(),
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

enum class UpdateInterval {
    MIN_15,
    MIN_30,
    HOUR_1,
    HOUR_2,
    HOUR_5,
    HOUR_12,
    HOUR_24,
    HOUR_48,
    WEEK_1,
    WEEK_2,
}
