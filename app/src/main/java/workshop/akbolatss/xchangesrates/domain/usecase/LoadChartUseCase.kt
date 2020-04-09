package workshop.akbolatss.xchangesrates.domain.usecase

import kotlinx.coroutines.CoroutineScope
import workshop.akbolatss.xchangesrates.base.BaseUseCase
import workshop.akbolatss.xchangesrates.base.resource.Either
import workshop.akbolatss.xchangesrates.base.resource.Failure
import workshop.akbolatss.xchangesrates.base.resource.flatMap
import workshop.akbolatss.xchangesrates.data.persistent.model.Chart
import workshop.akbolatss.xchangesrates.domain.repository.ChartRepository

class LoadChartUseCase(
    private val repository: ChartRepository
) : BaseUseCase<LoadChartUseCase.Params, Chart>() {

    override suspend fun run(params: Params, scope: CoroutineScope): Either<Failure, Chart> {
        return repository.loadChart(params.exchange, params.coin, params.currency, params.timing)
            .flatMap {
                val chart = repository.findBy(params.exchange, params.coin, params.currency)
                if (chart.isEmpty())
                    return Either.Left(Failure.ChartNotFound)

                return Either.Right(chart)
            }
    }

    data class Params(
        val exchange: String,
        val coin: String,
        val currency: String,
        val timing: String
    )
}
