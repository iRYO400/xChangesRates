package workshop.akbolatss.xchangesrates.domain.usecase

import kotlinx.coroutines.flow.Flow
import workshop.akbolatss.xchangesrates.base.BaseFlowUseCase
import workshop.akbolatss.xchangesrates.base.None
import workshop.akbolatss.xchangesrates.domain.model.Snapshot
import workshop.akbolatss.xchangesrates.domain.repository.SnapshotRepository

class FindAllSnapshotsUseCase(
    private val repository: SnapshotRepository
) : BaseFlowUseCase<FindAllSnapshotsUseCase.Params, List<Snapshot>>() {

    override fun run(params: Params): Flow<List<Snapshot>> = repository.findAll()

    data class Params(val none: None = None())
}
