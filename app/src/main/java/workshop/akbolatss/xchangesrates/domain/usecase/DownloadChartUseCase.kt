package workshop.akbolatss.xchangesrates.domain.usecase

import kotlinx.coroutines.CoroutineScope
import workshop.akbolatss.xchangesrates.base.BaseUseCase
import workshop.akbolatss.xchangesrates.base.resource.Either
import workshop.akbolatss.xchangesrates.base.resource.Failure
import workshop.akbolatss.xchangesrates.domain.model.Chart
import workshop.akbolatss.xchangesrates.domain.repository.ChartRepository

class DownloadChartUseCase(
    private val repository: ChartRepository
) : BaseUseCase<DownloadChartUseCase.Params, Chart>() {

    override suspend fun run(params: Params, scope: CoroutineScope): Either<Failure, Chart> {
        return repository.download(params.exchange, params.coin, params.currency, params.timing)
    }

    data class Params(
        val exchange: String,
        val coin: String,
        val currency: String,
        val timing: String
    )
}
