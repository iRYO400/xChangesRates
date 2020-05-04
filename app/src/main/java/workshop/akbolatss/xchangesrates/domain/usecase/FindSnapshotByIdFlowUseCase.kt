package workshop.akbolatss.xchangesrates.domain.usecase

import kotlinx.coroutines.flow.Flow
import workshop.akbolatss.xchangesrates.base.BaseFlowUseCase
import workshop.akbolatss.xchangesrates.domain.model.Snapshot
import workshop.akbolatss.xchangesrates.domain.repository.SnapshotRepository

class FindSnapshotByIdFlowUseCase(
    private val repository: SnapshotRepository
) : BaseFlowUseCase<FindSnapshotByIdFlowUseCase.Params, Snapshot>() {

    override fun run(params: Params): Flow<Snapshot> = repository.findByFlow(params.snapshotId)

    data class Params(val snapshotId: Long)
}
