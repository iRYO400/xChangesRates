package workshop.akbolatss.xchangesrates.domain.usecase

import kotlinx.coroutines.CoroutineScope
import workshop.akbolatss.xchangesrates.base.BaseUseCase
import workshop.akbolatss.xchangesrates.base.None
import workshop.akbolatss.xchangesrates.base.resource.Either
import workshop.akbolatss.xchangesrates.base.resource.Failure
import workshop.akbolatss.xchangesrates.domain.repository.SnapshotRepository

class GetSnapshotCountUseCase(
    private val repository: SnapshotRepository
): BaseUseCase<GetSnapshotCountUseCase.Params, Int>() {

    override suspend fun run(params: Params, scope: CoroutineScope): Either<Failure, Int> {
        return Either.Right(repository.getSnapshotCount())
    }

    data class Params(val none: None = None())
}
