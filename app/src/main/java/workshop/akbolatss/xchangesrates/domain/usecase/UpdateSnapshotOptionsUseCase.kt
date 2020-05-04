package workshop.akbolatss.xchangesrates.domain.usecase

import kotlinx.coroutines.CoroutineScope
import workshop.akbolatss.xchangesrates.base.BaseUseCase
import workshop.akbolatss.xchangesrates.base.None
import workshop.akbolatss.xchangesrates.base.resource.Either
import workshop.akbolatss.xchangesrates.base.resource.Failure
import workshop.akbolatss.xchangesrates.base.resource.flatMap
import workshop.akbolatss.xchangesrates.domain.model.ChangesForPeriod
import workshop.akbolatss.xchangesrates.domain.model.UpdateInterval
import workshop.akbolatss.xchangesrates.domain.repository.SnapshotRepository

class UpdateSnapshotOptionsUseCase(
    private val findSnapshotByIdUseCase: FindSnapshotByIdUseCase,
    private val snapshotRepository: SnapshotRepository
) : BaseUseCase<UpdateSnapshotOptionsUseCase.Params, None>() {

    override suspend fun run(params: Params, scope: CoroutineScope): Either<Failure, None> {
        return findSnapshotByIdUseCase(scope, FindSnapshotByIdUseCase.Params(params.snapshotId))
            .flatMap { snapshot ->
                val updatedOptions = snapshot.options.copy(
                    isNotificationEnabled = params.notificationEnabled,
                    isStick = params.stickNotification,
                    changesForPeriod = ChangesForPeriod.fromIndex(params.chartChangesPeriodIndex),
                    updateInterval = UpdateInterval.fromIndex(params.updateIntervalIndex)
                )
                snapshotRepository.updateOptions(updatedOptions)
            }
    }

    data class Params(
        val snapshotId: Long,
        val notificationEnabled: Boolean,
        val stickNotification: Boolean,
        val chartChangesPeriodIndex: Int,
        val updateIntervalIndex: Int
    )
}
