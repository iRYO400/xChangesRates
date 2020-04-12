package workshop.akbolatss.xchangesrates.domain.usecase

import kotlinx.coroutines.CoroutineScope
import workshop.akbolatss.xchangesrates.base.BaseUseCase
import workshop.akbolatss.xchangesrates.base.None
import workshop.akbolatss.xchangesrates.base.resource.Either
import workshop.akbolatss.xchangesrates.base.resource.Failure
import workshop.akbolatss.xchangesrates.domain.model.Chart
import workshop.akbolatss.xchangesrates.domain.repository.SnapshotRepository

class CreateOrUpdateSnapshotUseCase(
    private val snapshotRepository: SnapshotRepository,
    private val createSnapshotUseCase: CreateSnapshotUseCase
) : BaseUseCase<CreateOrUpdateSnapshotUseCase.Params, None>() {

    override suspend fun run(params: Params, scope: CoroutineScope): Either<Failure, None> {
        val snapshot = snapshotRepository.findBy(params.exchange, params.coin, params.currency)
        if (snapshot.isEmpty().not())
            return Either.Left(Failure.SnapshotAlreadyExists)

        return createSnapshotUseCase(
            scope,
            CreateSnapshotUseCase.Params(
                params.exchange,
                params.coin,
                params.currency,
                params.chart
            )
        )
    }

    data class Params(
        val exchange: String,
        val coin: String,
        val currency: String,
        val chart: Chart
    )
}
