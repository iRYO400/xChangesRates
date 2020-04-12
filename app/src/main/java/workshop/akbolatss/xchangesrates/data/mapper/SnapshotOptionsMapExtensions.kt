package workshop.akbolatss.xchangesrates.data.mapper

import workshop.akbolatss.xchangesrates.data.persistent.model.SnapshotOptionsEntity
import workshop.akbolatss.xchangesrates.domain.model.SnapshotOptions
import workshop.akbolatss.xchangesrates.utils.extension.defaultVal

object SnapshotOptionsMap {

    fun SnapshotOptions.map() = SnapshotOptionsEntity(
        id = id,
        snapshotId = defaultVal(),
        isSmartEnabled = isSmartEnabled,
        isStick = isStick,
        updateInterval = updateInterval
    )
}

object SnapshotOptionsEntityMap {

    fun SnapshotOptionsEntity.map() = SnapshotOptions(
        id = id,
        isSmartEnabled = isSmartEnabled,
        isStick = isStick,
        updateInterval = updateInterval
    )
}
