package workshop.akbolatss.xchangesrates.domain.usecase

import kotlinx.coroutines.CoroutineScope
import workshop.akbolatss.xchangesrates.base.BaseUseCase
import workshop.akbolatss.xchangesrates.base.None
import workshop.akbolatss.xchangesrates.base.resource.Either
import workshop.akbolatss.xchangesrates.base.resource.Failure

class FindSnapshotByIdUseCase(
) : BaseUseCase<FindSnapshotByIdUseCase.Params, None>() {

    override suspend fun run(params: Params, scope: CoroutineScope): Either<Failure, None> {
        return Either.Right(None()) //TODO failure case
    }

    data class Params(val itemId: Long)
}
