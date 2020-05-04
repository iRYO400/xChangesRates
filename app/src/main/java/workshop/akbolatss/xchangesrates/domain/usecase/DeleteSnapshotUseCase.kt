package workshop.akbolatss.xchangesrates.domain.usecase

import kotlinx.coroutines.CoroutineScope
import workshop.akbolatss.xchangesrates.base.BaseUseCase
import workshop.akbolatss.xchangesrates.base.None
import workshop.akbolatss.xchangesrates.base.resource.Either
import workshop.akbolatss.xchangesrates.base.resource.Failure
import workshop.akbolatss.xchangesrates.domain.repository.SnapshotRepository

class DeleteSnapshotUseCase(
    private val repository: SnapshotRepository
) :BaseUseCase<DeleteSnapshotUseCase.Params, None>(){

    override suspend fun run(params: Params, scope: CoroutineScope): Either<Failure, None> {
        return repository.deleteSnapshot(params.snapshotId)
    }

    data class Params(val snapshotId :Long)
}
