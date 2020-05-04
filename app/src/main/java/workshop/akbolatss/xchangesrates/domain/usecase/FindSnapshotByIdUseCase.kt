package workshop.akbolatss.xchangesrates.domain.usecase

import kotlinx.coroutines.CoroutineScope
import workshop.akbolatss.xchangesrates.base.BaseUseCase
import workshop.akbolatss.xchangesrates.base.resource.Either
import workshop.akbolatss.xchangesrates.base.resource.Failure
import workshop.akbolatss.xchangesrates.domain.model.Snapshot
import workshop.akbolatss.xchangesrates.domain.repository.SnapshotRepository

class FindSnapshotByIdUseCase(
    private val repository: SnapshotRepository
) : BaseUseCase<FindSnapshotByIdUseCase.Params, Snapshot>() {

    override suspend fun run(params: Params, scope: CoroutineScope): Either<Failure, Snapshot> {
        val snapshot = repository.findBy(params.snapshotId)
        if (snapshot.isEmpty())
            return Either.Left(Failure.SnapshotNotFound)

        return Either.Right(snapshot)
    }

    data class Params(val snapshotId: Long)
}
