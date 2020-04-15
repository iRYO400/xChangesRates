package workshop.akbolatss.xchangesrates.utils

object Constants {

    const val BASE_URL = "http://bitcoinstat.org/api_v3/"
    const val DB_SNAPS_NAME_NEW = "snapshots.db"

    const val HAWK_FIRST_START = "IsFirstStart"
    const val HAWK_LAST_UPDATE = "LastUpdateDate"
    const val HAWK_EXCHANGE_RESPONSE = "ExchangeResponse"

    const val HAWK_CHANNEL_CREATED = "isNotifyChannelCreated"
    const val HAWK_HISTORY_CODE = "SelectedHistory"
    const val HAWK_HISTORY_POS = "SelectedHistoryPos"

    const val MINUTES_10 = "10min"
    const val HOUR_1 = "1h"
    const val HOUR_3 = "3h"
    const val HOUR_12 = "12h"
    const val HOUR_24 = "24h"
    const val WEEK = "1w"
    const val MONTH = "1m"
    const val MONTH_3 = "3m"
    const val MONTH_6 = "6m"
    const val YEAR_1 = "1y"
    const val YEAR_2 = "2y"
    const val YEAR_5 = "5y"

    const val BUNDLE_CHART_ID = "chartId"
    const val BUNDLE_TIMING = "timingName"
    const val BUNDLE_POSITION = "chartPosition"
    const val BUNDLE_ISACTIVE = "isActive"

    const val NOTIFICATION_MAIN_ID = 1731
    const val NOTIFICATION_GROUP_SUMMARY = 1371
    const val NOTIFICATION_CHANNEL_ID = "workshop.akbolatss.xchangesrates.main"
    const val NOTIFICATION_CHANNEL_NAME = "Background service"
    const val NOTIFICATION_CHANNEL_DESCRIPTION = "For updating snapshots"

    const val HAWK_SHOWCASE_0_DONE = "showCase0Done"
    const val HAWK_SHOWCASE_1_DONE = "showCase1Done"
    const val HAWK_SHOWCASE_2_DONE = "showCase2Done"

}
