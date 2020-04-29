package workshop.akbolatss.xchangesrates.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import workshop.akbolatss.xchangesrates.base.BaseRepository
import workshop.akbolatss.xchangesrates.base.None
import workshop.akbolatss.xchangesrates.base.resource.Either
import workshop.akbolatss.xchangesrates.base.resource.Failure
import workshop.akbolatss.xchangesrates.data.mapper.SnapshotEntityMap.map
import workshop.akbolatss.xchangesrates.data.mapper.SnapshotMap.map
import workshop.akbolatss.xchangesrates.data.mapper.SnapshotOptionsEntityMap.map
import workshop.akbolatss.xchangesrates.data.mapper.SnapshotOptionsMap.map
import workshop.akbolatss.xchangesrates.data.persistent.dao.SnapshotDao
import workshop.akbolatss.xchangesrates.data.persistent.model.SnapshotEntity
import workshop.akbolatss.xchangesrates.data.persistent.model.SnapshotOptionsEntity
import workshop.akbolatss.xchangesrates.domain.model.Snapshot
import workshop.akbolatss.xchangesrates.domain.model.SnapshotOptions
import workshop.akbolatss.xchangesrates.domain.repository.SnapshotRepository

class SnapshotRepositoryImpl(
    private val snapshotDao: SnapshotDao
) : BaseRepository(), SnapshotRepository {

    override suspend fun getSnapshotCount(): Int =
        snapshotDao.getSnapshotCount()

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
            val snapshotEntity = updatedSnapshot.map()
            val optionsEntity = updatedSnapshot.options.map()
            Pair(snapshotEntity, optionsEntity)
        }, query = { (snapshot, options) ->
            snapshotDao.update(snapshot, options)
        })
    }

    override suspend fun updateOptions(updatedOptions: SnapshotOptions): Either<Failure, None> {
        return update(map = {
            updatedOptions.map()
        }, query = {
            snapshotDao.update(it)
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

    override fun findListFlow(): Flow<List<Snapshot>> = snapshotDao.findSnapshotListFlow()
        .combine(
            snapshotDao.findOptionsListFlow()
        ) { snapshotEntities, optionsEntities ->
            snapshotEntities.map { snapshotEntity ->
                val snapshotOptions = optionsEntities.find { snapshotOptionsEntity ->
                    snapshotOptionsEntity.snapshotId == snapshotEntity.id
                }
                snapshotEntity.map(snapshotOptions!!.map())
            }
        }
        .flowOn(Dispatchers.IO)

    override fun findByFlow(id: Long): Flow<Snapshot> = snapshotDao.findFlow(id)
        .combine(
            snapshotDao.findOptionsFlow(id)
        ) { snapshotEntity: SnapshotEntity, optionsEntity: SnapshotOptionsEntity ->
            snapshotEntity.map(optionsEntity.map())
        }
        .flowOn(Dispatchers.IO)
}
