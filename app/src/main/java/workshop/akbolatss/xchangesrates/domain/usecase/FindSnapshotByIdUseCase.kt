package workshop.akbolatss.xchangesrates.domain.usecase

import kotlinx.coroutines.CoroutineScope
import workshop.akbolatss.xchangesrates.base.BaseUseCase
import workshop.akbolatss.xchangesrates.base.resource.Either
import workshop.akbolatss.xchangesrates.base.resource.Failure
import workshop.akbolatss.xchangesrates.domain.repository.ChartRepository
import workshop.akbolatss.xchangesrates.model.response.ChartData

class FindSnapshotByIdUseCase(
        private val repository: ChartRepository
) : BaseUseCase<FindSnapshotByIdUseCase.Params, ChartData>() {

    override suspend fun run(params: Params, scope: CoroutineScope): Either<Failure, ChartData> {
        return Either.Right(repository.findBy(params.itemId)) //TODO failure case
    }

    data class Params(val itemId: Long)
}
