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

enum class ChangesForPeriod(val queryParam: String, val indexUi: Int) {
    MINUTES_10("10min", 0),
    HOUR_1("1h", 1),
    HOUR_3("3h", 2),
    HOUR_12("12h", 3),
    HOUR_24("24h", 4),
    WEEK("1w", 5),
    MONTH("1m", 6),
    MONTH_3("3m", 7),
    MONTH_6("6m", 8),
    YEAR_1("1y", 9),
    YEAR_2("2y", 10),
    YEAR_5("5y", 11);

    companion object {
        fun fromIndex(indexUi: Int): ChangesForPeriod {
            return when (indexUi) {
                0 -> MINUTES_10
                1 -> HOUR_1
                2 -> HOUR_3
                3 -> HOUR_12
                4 -> HOUR_24
                5 -> WEEK
                6 -> MONTH
                7 -> MONTH_3
                8 -> MONTH_6
                9 -> YEAR_1
                10 -> YEAR_2
                11 -> YEAR_5
                else -> HOUR_1
            }
        }
    }
}

enum class UpdateInterval(val seconds: Long, val indexUi: Int) {
    MIN_15(15 * 60, 0),
    MIN_30(30 * 60, 1),
    HOUR_1(60 * 60, 2),
    HOUR_2(2 * 60 * 60, 3),
    HOUR_5(5 * 60 * 60, 4),
    HOUR_12(12 * 60 * 60, 5),
    HOUR_24(24 * 60 * 60, 6),
    HOUR_48(48 * 60 * 60, 7),
    WEEK_1(168 * 60 * 60, 8),
    WEEK_2(336 * 60 * 60, 9);

    companion object {
        fun fromIndex(indexUi: Int): UpdateInterval {
            return when (indexUi) {
                0 -> MIN_15
                1 -> MIN_30
                2 -> HOUR_1
                3 -> HOUR_2
                4 -> HOUR_5
                5 -> HOUR_12
                6 -> HOUR_24
                7 -> HOUR_48
                8 -> WEEK_1
                9 -> WEEK_2
                else -> HOUR_1
            }
        }
    }
}
