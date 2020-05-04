package workshop.akbolatss.xchangesrates.domain.usecase

import kotlinx.coroutines.CoroutineScope
import workshop.akbolatss.xchangesrates.base.BaseUseCase
import workshop.akbolatss.xchangesrates.base.resource.Either
import workshop.akbolatss.xchangesrates.base.resource.Failure
import workshop.akbolatss.xchangesrates.base.resource.flatMap
import workshop.akbolatss.xchangesrates.domain.model.Snapshot
import workshop.akbolatss.xchangesrates.domain.repository.SnapshotRepository

class ToggleNotificationUseCase(
    private val snapshotRepository: SnapshotRepository,
    private val findSnapshotByIdUseCase: FindSnapshotByIdUseCase
) : BaseUseCase<ToggleNotificationUseCase.Params, Snapshot>() {

    override suspend fun run(params: Params, scope: CoroutineScope): Either<Failure, Snapshot> {
        return findSnapshotByIdUseCase(scope, FindSnapshotByIdUseCase.Params(params.snapshotId))
            .flatMap { snapshot ->
                val newNotificationState = !snapshot.options.isNotificationEnabled
                val updatedSnapshotOptions = snapshot.options.copy(
                    isNotificationEnabled = newNotificationState
                )
                val updatedSnapshot = snapshot.copy(
                    options = updatedSnapshotOptions
                )

                snapshotRepository.update(updatedSnapshot)
            }.flatMap {
                findSnapshotByIdUseCase(scope, FindSnapshotByIdUseCase.Params(params.snapshotId))
            }
    }

    data class Params(val snapshotId: Long)
}
