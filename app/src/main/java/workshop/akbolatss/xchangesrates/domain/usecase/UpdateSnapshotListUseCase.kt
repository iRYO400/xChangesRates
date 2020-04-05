package workshop.akbolatss.xchangesrates.domain.usecase

import kotlinx.coroutines.CoroutineScope
import workshop.akbolatss.xchangesrates.base.BaseUseCase
import workshop.akbolatss.xchangesrates.base.None
import workshop.akbolatss.xchangesrates.base.resource.Either
import workshop.akbolatss.xchangesrates.base.resource.Failure
import workshop.akbolatss.xchangesrates.base.resource.doOnFailure
import workshop.akbolatss.xchangesrates.base.resource.flatMap

class UpdateSnapshotListUseCase(
        private val findAllSnapshotsUseCase: FindAllSnapshotsUseCase,
        private val updateSingleSnapshotUseCase: UpdateSingleSnapshotUseCase
) : BaseUseCase<UpdateSnapshotListUseCase.Params, None>() {

    override suspend fun run(params: Params, scope: CoroutineScope): Either<Failure, None> {
        return findAllSnapshotsUseCase(scope, FindAllSnapshotsUseCase.Params())
                .flatMap { charts ->
                    charts.forEach { chart ->
                        updateSingleSnapshotUseCase(scope, UpdateSingleSnapshotUseCase.Params(chart.id))
                                .doOnFailure { failure ->
                                    return@flatMap Either.Left(failure)
                                }
                    }

                    return@flatMap Either.Right(None())
                }
    }

    data class Params(val none: None = None())
}
