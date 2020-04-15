package workshop.akbolatss.xchangesrates.domain.usecase

import kotlinx.coroutines.flow.Flow
import workshop.akbolatss.xchangesrates.base.BaseFlowUseCase
import workshop.akbolatss.xchangesrates.base.None
import workshop.akbolatss.xchangesrates.domain.model.Snapshot
import workshop.akbolatss.xchangesrates.domain.repository.SnapshotRepository

class FindAllSnapshotsFlowUseCase(
    private val repository: SnapshotRepository
) : BaseFlowUseCase<FindAllSnapshotsFlowUseCase.Params, List<Snapshot>>() {

    override fun run(params: Params): Flow<List<Snapshot>> = repository.findListFlow()

    data class Params(val none: None = None())
}
