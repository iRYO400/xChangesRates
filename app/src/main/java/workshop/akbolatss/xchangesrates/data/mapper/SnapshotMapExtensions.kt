package workshop.akbolatss.xchangesrates.data.mapper

import workshop.akbolatss.xchangesrates.data.persistent.model.DBSnapshot
import workshop.akbolatss.xchangesrates.domain.model.Snapshot

object SnapshotMap {

    fun Snapshot.map() = DBSnapshot(
        id = id,
        exchangerName = exchangerName,
        coin = coin,
        currency = currency,
        source = source,
        updateTime = updateTime,
        rate = rate,
        high = high,
        low = low
    )
}

object DBSnapshotMap {

    fun DBSnapshot.map() = Snapshot(
        id = id,
        exchangerName = exchangerName,
        coin = coin,
        currency = currency,
        source = source,
        updateTime = updateTime,
        rate = rate,
        high = high,
        low = low
    )

    fun List<DBSnapshot>.map() =
        this.map { it.map() }
}
