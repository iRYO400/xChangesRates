package workshop.akbolatss.xchangesrates.domain.usecase

import kotlinx.coroutines.CoroutineScope
import workshop.akbolatss.xchangesrates.base.BaseUseCase
import workshop.akbolatss.xchangesrates.base.None
import workshop.akbolatss.xchangesrates.base.resource.Either
import workshop.akbolatss.xchangesrates.base.resource.Failure
import workshop.akbolatss.xchangesrates.domain.repository.SnapshotRepository

class ToggleNotificationUseCase(
    private val snapshotRepository: SnapshotRepository
) : BaseUseCase<ToggleNotificationUseCase.Params, None>() {

    override suspend fun run(params: Params, scope: CoroutineScope): Either<Failure, None> {
        val snapshot = snapshotRepository.findBy(params.snapshotId)
        if (snapshot.isEmpty())
            return Either.Left(Failure.SnapshotNotFound)

        val newNotificationState = !snapshot.options.isNotificationEnabled
        val updatedSnapshotOptions = snapshot.options.copy(
            isNotificationEnabled = newNotificationState
        )
        val updatedSnapshot = snapshot.copy(
            options = updatedSnapshotOptions
        )

        return snapshotRepository.update(updatedSnapshot)

    }

    data class Params(val snapshotId: Long)
}
