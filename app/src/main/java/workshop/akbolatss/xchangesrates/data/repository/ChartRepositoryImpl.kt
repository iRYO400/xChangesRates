package workshop.akbolatss.xchangesrates.data.repository

import workshop.akbolatss.xchangesrates.base.BaseRepository
import workshop.akbolatss.xchangesrates.base.resource.Either
import workshop.akbolatss.xchangesrates.base.resource.Failure
import workshop.akbolatss.xchangesrates.data.remote.service.ApiService
import workshop.akbolatss.xchangesrates.domain.model.Chart
import workshop.akbolatss.xchangesrates.domain.model.ChartDot
import workshop.akbolatss.xchangesrates.domain.repository.ChartRepository

class ChartRepositoryImpl(
    private val apiService: ApiService
) : BaseRepository(), ChartRepository {

    override suspend fun download(
        exchange: String,
        coin: String,
        currency: String,
        timing: String
    ): Either<Failure, Chart> {
        return apiCall(call = {
            apiService.getChart(exchange, coin, currency, timing)
        }, mapResponse = { response ->
            Chart(
                rate = response.data.info.last,
                high = response.data.info.high,
                low = response.data.info.low,
                change = response.data.info.change,
                change24 = response.data.info.change24,
                chartDots = response.data.chartDots.map {
                    ChartDot(it.timestamp, it.price)
                }
            )
        })
    }

}
