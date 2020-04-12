package workshop.akbolatss.xchangesrates.data.repository

import workshop.akbolatss.xchangesrates.base.BaseRepository
import workshop.akbolatss.xchangesrates.base.None
import workshop.akbolatss.xchangesrates.base.resource.Either
import workshop.akbolatss.xchangesrates.base.resource.Failure
import workshop.akbolatss.xchangesrates.base.resource.flatMap
import workshop.akbolatss.xchangesrates.data.persistent.dao.ExchangeDao
import workshop.akbolatss.xchangesrates.data.persistent.model.ExchangeEntity
import workshop.akbolatss.xchangesrates.data.remote.model.ExchangeResponse
import workshop.akbolatss.xchangesrates.data.remote.service.ApiService
import workshop.akbolatss.xchangesrates.domain.repository.ExchangeRepository
import java.util.*

class ExchangeRepositoryImpl(
    private val apiService: ApiService,
    private val exchangeDao: ExchangeDao
) : BaseRepository(), ExchangeRepository {

    override suspend fun downloadAndSave(): Either<Failure, None> {
        return apiCall(call = {
            apiService.downloadExchanges()
        }, mapResponse = {
            it.data
        }).flatMap { exchanges ->
            saveExchanges(exchanges)
        }
    }

    private suspend fun saveExchanges(response: List<ExchangeResponse>): Either<Failure, None> {
        return insertList(map = {
            response.map {
                ExchangeEntity(
                    id = it.ident,
                    caption = it.caption,
                    currencies = it.currencies,
                    updateTime = Date()
                )
            }
        }, query = { exchanges ->
            exchangeDao.insertList(exchanges)
        })
    }

    override suspend fun findAll(): List<ExchangeEntity> =
        exchangeDao.findAll()

}
