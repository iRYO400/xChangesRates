package workshop.akbolatss.xchangesrates.domain.repository

import workshop.akbolatss.xchangesrates.base.None
import workshop.akbolatss.xchangesrates.base.resource.Either
import workshop.akbolatss.xchangesrates.base.resource.Failure
import workshop.akbolatss.xchangesrates.data.persistent.model.ExchangeEntity

interface ExchangeRepository {

    suspend fun downloadAndSave(): Either<Failure, None>

    suspend fun findAll(): List<ExchangeEntity>
}
