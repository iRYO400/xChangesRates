package workshop.akbolatss.xchangesrates.domain.usecase

import kotlinx.coroutines.CoroutineScope
import workshop.akbolatss.xchangesrates.base.BaseUseCase
import workshop.akbolatss.xchangesrates.base.None
import workshop.akbolatss.xchangesrates.base.resource.Either
import workshop.akbolatss.xchangesrates.base.resource.Failure
import workshop.akbolatss.xchangesrates.domain.model.Snapshot
import workshop.akbolatss.xchangesrates.domain.repository.ChartRepository
import workshop.akbolatss.xchangesrates.domain.repository.SnapshotRepository
import workshop.akbolatss.xchangesrates.utils.extension.localId
import java.util.*

class CreateSnapshotUseCase(
    private val chartRepository: ChartRepository,
    private val snapshotRepository: SnapshotRepository
) : BaseUseCase<CreateSnapshotUseCase.Params, None>() {

    override suspend fun run(params: Params, scope: CoroutineScope): Either<Failure, None> {
        val chart = chartRepository.findBy(params.exchange, params.coin, params.currency)
        if (chart.isEmpty())
            return Either.Left(Failure.ChartNotFound)

        val snapshot = Snapshot(
            id = localId(),
            exchangerName = chart.exchange,
            coin = chart.coin,
            currency = chart.currency,
            source = chart.source,
            updateTime = Date(),
            rate = chart.info.last,
            high = chart.info.high,
            low = chart.info.low
        )

        return snapshotRepository.create(snapshot)
    }

    data class Params(
        val exchange: String,
        val coin: String,
        val currency: String
    )
}
