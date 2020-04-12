package workshop.akbolatss.xchangesrates.data.mapper

import workshop.akbolatss.xchangesrates.data.persistent.model.PriceByTimeEntity
import workshop.akbolatss.xchangesrates.data.persistent.model.SnapshotEntity
import workshop.akbolatss.xchangesrates.domain.model.PriceByTime
import workshop.akbolatss.xchangesrates.domain.model.Snapshot

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
        charts = charts.map {
            PriceByTimeEntity(it.timestamp, it.price)
        }
    )
}

object SnapshotEntityMap {

    fun SnapshotEntity.map() = Snapshot(
        id = id,
        exchangerName = exchangerName,
        coin = coin,
        currency = currency,
        updateTime = updateTime,
        rate = rate,
        high = high,
        low = low,
        charts = charts.map {
            PriceByTime(it.timestamp, it.price)
        }
    )

    fun List<SnapshotEntity>.map() =
        this.map { it.map() }
}
