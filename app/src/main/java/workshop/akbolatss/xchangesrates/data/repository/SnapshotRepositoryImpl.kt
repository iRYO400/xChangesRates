package workshop.akbolatss.xchangesrates.data.repository

import workshop.akbolatss.xchangesrates.base.BaseRepository
import workshop.akbolatss.xchangesrates.base.None
import workshop.akbolatss.xchangesrates.base.resource.Either
import workshop.akbolatss.xchangesrates.base.resource.Failure
import workshop.akbolatss.xchangesrates.data.mapper.DBSnapshotMap.map
import workshop.akbolatss.xchangesrates.data.mapper.SnapshotMap.map
import workshop.akbolatss.xchangesrates.data.persistent.dao.SnapshotDao
import workshop.akbolatss.xchangesrates.domain.model.Snapshot
import workshop.akbolatss.xchangesrates.domain.repository.SnapshotRepository

class SnapshotRepositoryImpl(
    private val snapshotDao: SnapshotDao
) : BaseRepository(), SnapshotRepository {

    override suspend fun create(snapshot: Snapshot): Either<Failure, None> {
        return insert(
            map = {
                snapshot.map()
            }, query = {
                snapshotDao.create(it)
            }
        )
    }

    override suspend fun findBy(exchange: String, coin: String, currency: String): Snapshot =
        snapshotDao.findBy(exchange, coin, currency)?.map() ?: Snapshot.empty()

}
