package workshop.akbolatss.xchangesrates.data.mapper

import workshop.akbolatss.xchangesrates.data.persistent.model.SnapshotOptionsEntity
import workshop.akbolatss.xchangesrates.domain.model.SnapshotOptions

object SnapshotOptionsMap {

    fun SnapshotOptions.map() = SnapshotOptionsEntity(
        id = id,
        snapshotId = snapshotId,
        isSmartEnabled = isSmartEnabled,
        isStick = isStick,
        updateInterval = updateInterval,
        changesForPeriod = changesForPeriod,
        isNotificationEnabled = isNotificationEnabled
    )
}

object SnapshotOptionsEntityMap {

    fun SnapshotOptionsEntity.map() = SnapshotOptions(
        id = id,
        snapshotId = snapshotId,
        isSmartEnabled = isSmartEnabled,
        isStick = isStick,
        updateInterval = updateInterval,
        changesForPeriod = changesForPeriod,
        isNotificationEnabled = isNotificationEnabled
    )
}
