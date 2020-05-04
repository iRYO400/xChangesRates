package workshop.akbolatss.xchangesrates.data.persistent.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import workshop.akbolatss.xchangesrates.domain.model.ChangesForPeriod
import workshop.akbolatss.xchangesrates.domain.model.UpdateInterval

@Entity(
    tableName = "snapshot_options"
)
data class SnapshotOptionsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ForeignKey(
        entity = SnapshotEntity::class,
        parentColumns = ["id"],
        childColumns = ["snapshotId"],
        onDelete = ForeignKey.CASCADE
    )
    val snapshotId: Long,
    val isNotificationEnabled: Boolean,
    val isSmartEnabled: Boolean = true,
    val isStick: Boolean = false,
    val updateInterval: UpdateInterval = UpdateInterval.HOUR_1,
    val changesForPeriod: ChangesForPeriod = ChangesForPeriod.HOUR_24
)
