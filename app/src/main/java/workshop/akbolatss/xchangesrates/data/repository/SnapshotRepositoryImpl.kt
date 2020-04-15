package workshop.akbolatss.xchangesrates.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.zip
import workshop.akbolatss.xchangesrates.base.BaseRepository
import workshop.akbolatss.xchangesrates.base.None
import workshop.akbolatss.xchangesrates.base.resource.Either
import workshop.akbolatss.xchangesrates.base.resource.Failure
import workshop.akbolatss.xchangesrates.data.mapper.SnapshotEntityMap.map
import workshop.akbolatss.xchangesrates.data.mapper.SnapshotMap.map
import workshop.akbolatss.xchangesrates.data.mapper.SnapshotOptionsEntityMap.map
import workshop.akbolatss.xchangesrates.data.mapper.SnapshotOptionsMap.map
import workshop.akbolatss.xchangesrates.data.persistent.dao.SnapshotDao
import workshop.akbolatss.xchangesrates.domain.model.Snapshot
import workshop.akbolatss.xchangesrates.domain.repository.SnapshotRepository

class SnapshotRepositoryImpl(
    private val snapshotDao: SnapshotDao
) : BaseRepository(), SnapshotRepository {

    override suspend fun create(snapshot: Snapshot): Either<Failure, None> {
        return insert(
            map = {
                val snapshotEntity = snapshot.map()
                val optionsEntity = snapshot.options.map()
                Pair(snapshotEntity, optionsEntity)
            }, query = { (snapshot, options) ->
                snapshotDao.create(snapshot, options)
            }
        )
    }

    override suspend fun update(updatedSnapshot: Snapshot): Either<Failure, None> {
        return update(map = {
            updatedSnapshot.map()
        }, query = { snapshot ->
            snapshotDao.update(snapshot)
        })
    }

    override suspend fun findBy(id: Long): Snapshot {
        val snapshotEntity = snapshotDao.findBy(id) ?: return Snapshot.empty()
        val snapshotOptionsEntity = snapshotDao.findOptions(id)
        val snapshotOptions = snapshotOptionsEntity.map()

        return snapshotEntity.map(snapshotOptions)
    }

    override suspend fun findBy(exchange: String, coin: String, currency: String): Snapshot {
        val snapshotEntity = snapshotDao.findBy(exchange, coin, currency) ?: return Snapshot.empty()
        val snapshotOptionsEntity =
            snapshotDao.findOptions(snapshotEntity.id)

        val snapshotOptions = snapshotOptionsEntity.map()
        return snapshotEntity.map(snapshotOptions)
    }

    override fun findAll(): Flow<List<Snapshot>> = snapshotDao.findAll()
        .zip(snapshotDao.findOptionsList()) { snapshotEntities, snapshotOptionsEntities ->
            snapshotEntities.map { snapshotEntity ->
                val snapshotOptions = snapshotOptionsEntities.find { snapshotOptionsEntity ->
                    snapshotOptionsEntity.snapshotId == snapshotEntity.id
                }
                snapshotEntity.map(snapshotOptions!!.map())
            }
        }
}
