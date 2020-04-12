package workshop.akbolatss.xchangesrates.data.persistent.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

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
    val isSmartEnabled: Boolean = true,
    val isStick: Boolean = false,
    val updateInterval: UpdateInterval = UpdateInterval.HOUR_1
)

enum class UpdateInterval {
    MIN_15,
    MIN_30,
    HOUR_1,
    HOUR_2,
    HOUR_5,
    HOUR_12,
    HOUR_24,
    HOUR_48,
    WEEK_1,
    WEEK_2,
}
