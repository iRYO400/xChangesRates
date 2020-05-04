package workshop.akbolatss.xchangesrates.data.mapper

import workshop.akbolatss.xchangesrates.data.persistent.model.ChartDotEntity
import workshop.akbolatss.xchangesrates.data.persistent.model.SnapshotEntity
import workshop.akbolatss.xchangesrates.domain.model.ChartDot
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
        chartDots = chartDots.map {
            ChartDotEntity(it.timestamp, it.price)
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
        chartDots = chartDots.map {
            ChartDot(it.timestamp, it.price)
        }
    )
}
