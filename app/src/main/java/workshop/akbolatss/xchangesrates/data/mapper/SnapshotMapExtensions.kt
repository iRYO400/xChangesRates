package workshop.akbolatss.xchangesrates.data.mapper

import workshop.akbolatss.xchangesrates.data.persistent.model.PriceByTimeEntity
import workshop.akbolatss.xchangesrates.data.persistent.model.SnapshotEntity
import workshop.akbolatss.xchangesrates.domain.model.PriceByTime
import workshop.akbolatss.xchangesrates.domain.model.Snapshot
import workshop.akbolatss.xchangesrates.domain.model.SnapshotOptions

object SnapshotMap {

    fun Snapshot.map() = SnapshotEntity(
        id = id,
        exchangerName = exchangerName,
        coin = coin,
        currency = currency,
        updateTime = updateTime,
        rate = rate,
        high = high,
        low = low,
        change = change,
        change24 = change24,
        charts = charts.map {
            PriceByTimeEntity(it.timestamp, it.price)
        }
    )
}


object SnapshotEntityMap {

    fun SnapshotEntity.map(snapshotOptions: SnapshotOptions) = Snapshot(
        id = id,
        exchangerName = exchangerName,
        coin = coin,
        currency = currency,
        updateTime = updateTime,
        rate = rate,
        high = high,
        low = low,
        change = change,
        change24 = change24,
        options = snapshotOptions,
        charts = charts.map {
            PriceByTime(it.timestamp, it.price)
        }
    )
}
