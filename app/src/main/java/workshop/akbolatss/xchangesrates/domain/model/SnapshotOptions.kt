package workshop.akbolatss.xchangesrates.domain.model

import workshop.akbolatss.xchangesrates.data.persistent.model.UpdateInterval
import workshop.akbolatss.xchangesrates.utils.extension.defaultVal

data class SnapshotOptions(
    val id: Long,
    val isSmartEnabled: Boolean,
    val isStick: Boolean,
    val updateInterval: UpdateInterval
) {
    companion object {
        fun empty() = SnapshotOptions(
            id = defaultVal(),
            isSmartEnabled = false,
            isStick = false,
            updateInterval = UpdateInterval.HOUR_1
        )
    }
}
