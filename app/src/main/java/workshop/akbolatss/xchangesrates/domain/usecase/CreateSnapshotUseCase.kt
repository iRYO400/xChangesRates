package workshop.akbolatss.xchangesrates.domain.usecase

import kotlinx.coroutines.CoroutineScope
import workshop.akbolatss.xchangesrates.base.BaseUseCase
import workshop.akbolatss.xchangesrates.base.None
import workshop.akbolatss.xchangesrates.base.resource.Either
import workshop.akbolatss.xchangesrates.base.resource.Failure
import workshop.akbolatss.xchangesrates.data.persistent.model.UpdateInterval
import workshop.akbolatss.xchangesrates.domain.model.Chart
import workshop.akbolatss.xchangesrates.domain.model.Snapshot
import workshop.akbolatss.xchangesrates.domain.model.SnapshotOptions
import workshop.akbolatss.xchangesrates.domain.repository.SnapshotRepository
import workshop.akbolatss.xchangesrates.utils.extension.localId
import java.util.*

class CreateSnapshotUseCase(
    private val snapshotRepository: SnapshotRepository
) : BaseUseCase<CreateSnapshotUseCase.Params, None>() {

    override suspend fun run(params: Params, scope: CoroutineScope): Either<Failure, None> {
        val options = SnapshotOptions(
            id = localId(),
            isSmartEnabled = false,
            isStick = false,
            updateInterval = UpdateInterval.HOUR_1
        )

        val snapshot = Snapshot(
            id = localId(),
            exchangerName = params.exchange,
            coin = params.coin,
            currency = params.currency,
            rate = params.chart.rate,
            high = params.chart.high,
            low = params.chart.low,
            updateTime = Date(),
            options = options,
            charts = params.chart.units
        )

        return snapshotRepository.create(snapshot)
    }

    data class Params(
        val exchange: String,
        val coin: String,
        val currency: String,
        val chart: Chart
    )
}
