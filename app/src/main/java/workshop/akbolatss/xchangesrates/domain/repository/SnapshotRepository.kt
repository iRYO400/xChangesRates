package workshop.akbolatss.xchangesrates.domain.repository

import kotlinx.coroutines.flow.Flow
import workshop.akbolatss.xchangesrates.base.None
import workshop.akbolatss.xchangesrates.base.resource.Either
import workshop.akbolatss.xchangesrates.base.resource.Failure
import workshop.akbolatss.xchangesrates.domain.model.Snapshot
import workshop.akbolatss.xchangesrates.domain.model.SnapshotOptions

interface SnapshotRepository {

    suspend fun getSnapshotCount(): Int

    suspend fun create(snapshot: Snapshot): Either<Failure, None>

    suspend fun update(updatedSnapshot: Snapshot): Either<Failure, None>

    suspend fun updateOptions(updatedOptions: SnapshotOptions): Either<Failure, None>

    suspend fun deleteSnapshot(snapshotId: Long): Either<Failure, None>

    suspend fun findBy(id: Long): Snapshot

    suspend fun findBy(exchange: String, coin: String, currency: String): Snapshot

    fun findListFlow(): Flow<List<Snapshot>>

    fun findByFlow(id:Long): Flow<Snapshot>
}
