package workshop.akbolatss.xchangesrates.data.persistent.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import workshop.akbolatss.xchangesrates.data.persistent.model.SnapshotEntity
import workshop.akbolatss.xchangesrates.data.persistent.model.SnapshotOptionsEntity

@Dao
abstract class SnapshotDao {

    @Query("SELECT COUNT(id) FROM snapshot")
    abstract suspend fun getSnapshotCount(): Int

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
    protected abstract suspend fun create(snapshotOptions: SnapshotOptionsEntity)

    @Transaction
    open suspend fun update(
        snapshot: SnapshotEntity,
        snapshotOptions: SnapshotOptionsEntity
    ): Int {
        update(snapshot)
        update(snapshotOptions)
        return 1
    }

    @Update
    protected abstract suspend fun update(snapshot: SnapshotEntity): Int

    @Update
    abstract suspend fun update(snapshotOptions: SnapshotOptionsEntity): Int

    @Query("DELETE FROM snapshot WHERE id = :snapshotId")
    abstract suspend fun delete(snapshotId: Long): Int

    @Query("SELECT * FROM snapshot WHERE exchangerName = :exchange AND coin = :coin AND currency = :currency")
    abstract suspend fun findBy(exchange: String, coin: String, currency: String): SnapshotEntity?

    @Query("SELECT * FROM snapshot WHERE id = :id")
    abstract suspend fun findBy(id: Long): SnapshotEntity?

    @Query("SELECT * FROM snapshot_options WHERE snapshotId = :snapshotId")
    abstract suspend fun findOptions(snapshotId: Long): SnapshotOptionsEntity

    @Query("SELECT * FROM snapshot WHERE id = :id")
    abstract fun findFlow(id: Long): Flow<SnapshotEntity?>

    @Query("SELECT * FROM snapshot_options WHERE snapshotId = :snapshotId")
    abstract fun findOptionsFlow(snapshotId: Long): Flow<SnapshotOptionsEntity>

    @Query("SELECT * FROM snapshot ORDER BY updateTime ASC")
    abstract fun findSnapshotListFlow(): Flow<List<SnapshotEntity>>

    @Query("SELECT * FROM snapshot_options")
    abstract fun findOptionsListFlow(): Flow<List<SnapshotOptionsEntity>>

}
