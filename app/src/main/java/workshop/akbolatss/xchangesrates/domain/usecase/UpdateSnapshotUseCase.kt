package workshop.akbolatss.xchangesrates.domain.usecase

import kotlinx.coroutines.CoroutineScope
import workshop.akbolatss.xchangesrates.base.BaseUseCase
import workshop.akbolatss.xchangesrates.base.None
import workshop.akbolatss.xchangesrates.base.resource.Either
import workshop.akbolatss.xchangesrates.base.resource.Failure
import workshop.akbolatss.xchangesrates.base.resource.flatMap
import workshop.akbolatss.xchangesrates.base.resource.zipWith
import workshop.akbolatss.xchangesrates.domain.repository.ChartRepository
import workshop.akbolatss.xchangesrates.domain.repository.SnapshotRepository
import java.util.*

class UpdateSnapshotUseCase(
    private val snapshotRepository: SnapshotRepository,
    private val findSnapshotByIdUseCase: FindSnapshotByIdUseCase,
    private val chartRepository: ChartRepository
) : BaseUseCase<UpdateSnapshotUseCase.Params, None>() {

    override suspend fun run(params: Params, scope: CoroutineScope): Either<Failure, None> {
        return findSnapshotByIdUseCase(scope, FindSnapshotByIdUseCase.Params(params.snapshotId))
            .zipWith { snapshot ->
                chartRepository.download(
                    snapshot.exchangerName,
                    snapshot.coin,
                    snapshot.currency,
                    snapshot.options.changesForPeriod.queryParam
                )
            }.flatMap { (snapshot, chart) ->
                val updatedSnapshot = snapshot.copy(
                    rate = chart.rate,
                    high = chart.high,
                    low = chart.low,
                    change = chart.change,
                    change24 = chart.change24,
                    charts = chart.units,
                    updateTime = Date()
                )
                snapshotRepository.update(updatedSnapshot)
            }
    }

    data class Params(val snapshotId: Long)
}
