package workshop.akbolatss.xchangesrates.domain.usecase

import kotlinx.coroutines.CoroutineScope
import workshop.akbolatss.xchangesrates.base.BaseUseCase
import workshop.akbolatss.xchangesrates.base.None
import workshop.akbolatss.xchangesrates.base.resource.Either
import workshop.akbolatss.xchangesrates.base.resource.Failure
import workshop.akbolatss.xchangesrates.domain.repository.ChartRepository
import workshop.akbolatss.xchangesrates.model.response.ChartData

class FindAllSnapshotsUseCase(
        private val repository: ChartRepository
) : BaseUseCase<FindAllSnapshotsUseCase.Params, List<ChartData>>() {

    override suspend fun run(params: Params, scope: CoroutineScope): Either<Failure, List<ChartData>> {
        return Either.Right(repository.findList())
    }

    data class Params(val none: None = None())
}
