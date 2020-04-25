package workshop.akbolatss.xchangesrates.data.repository

import workshop.akbolatss.xchangesrates.base.BaseRepository
import workshop.akbolatss.xchangesrates.base.None
import workshop.akbolatss.xchangesrates.base.resource.Either
import workshop.akbolatss.xchangesrates.base.resource.Failure
import workshop.akbolatss.xchangesrates.base.resource.flatMap
import workshop.akbolatss.xchangesrates.data.mapper.ExchangeEntityMap.map
import workshop.akbolatss.xchangesrates.data.mapper.ExchangeResponseMap.map
import workshop.akbolatss.xchangesrates.data.persistent.dao.ExchangeDao
import workshop.akbolatss.xchangesrates.data.remote.model.ExchangeResponse
import workshop.akbolatss.xchangesrates.data.remote.service.ApiService
import workshop.akbolatss.xchangesrates.domain.model.Exchange
import workshop.akbolatss.xchangesrates.domain.repository.ExchangeRepository

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
                it.map()
            }
        }, query = { exchanges ->
            exchangeDao.insertList(exchanges)
        })
    }

    override suspend fun findAll(): List<Exchange> =
        exchangeDao.findAll().map { it.map() }

}
