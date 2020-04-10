package workshop.akbolatss.xchangesrates.domain.repository

import kotlinx.coroutines.flow.Flow
import workshop.akbolatss.xchangesrates.base.None
import workshop.akbolatss.xchangesrates.base.resource.Either
import workshop.akbolatss.xchangesrates.base.resource.Failure
import workshop.akbolatss.xchangesrates.data.persistent.model.DBSnapshot
import workshop.akbolatss.xchangesrates.domain.model.Snapshot

interface SnapshotRepository {

    suspend fun create(snapshot: Snapshot): Either<Failure, None>

    suspend fun findBy(exchange: String, coin: String, currency: String): Snapshot

    fun findAll(): Flow<List<Snapshot>>
}
