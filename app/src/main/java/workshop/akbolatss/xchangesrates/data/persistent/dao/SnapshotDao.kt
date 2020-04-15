package workshop.akbolatss.xchangesrates.data.persistent.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import workshop.akbolatss.xchangesrates.data.persistent.model.SnapshotEntity
import workshop.akbolatss.xchangesrates.data.persistent.model.SnapshotOptionsEntity

@Dao
abstract class SnapshotDao {

    @Transaction
    open suspend fun create(
        snapshot: SnapshotEntity,
        snapshotOptions: SnapshotOptionsEntity
    ): Long {
        val createdId = create(snapshot)
        val withForeignKey = snapshotOptions.copy(snapshotId = createdId)
        create(withForeignKey)
        return 1L
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun create(snapshot: SnapshotEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract fun create(snapshotOptions: SnapshotOptionsEntity)

    @Update
    abstract suspend fun update(snapshot: SnapshotEntity): Int

    @Query("SELECT * FROM snapshot WHERE exchangerName = :exchange AND coin = :coin AND currency = :currency")
    abstract suspend fun findBy(exchange: String, coin: String, currency: String): SnapshotEntity?

    @Query("SELECT * FROM snapshot WHERE id = :id")
    abstract suspend fun findBy(id: Long): SnapshotEntity?

    @Query("SELECT * FROM snapshot ORDER BY updateTime ASC")
    abstract fun findAll(): Flow<List<SnapshotEntity>>

    @Query("SELECT * FROM snapshot_options WHERE snapshotId = :snapshotId")
    abstract suspend fun findOptions(snapshotId: Long): SnapshotOptionsEntity

    @Query("SELECT * FROM snapshot_options")
    abstract fun findOptionsList(): Flow<List<SnapshotOptionsEntity>>
}
