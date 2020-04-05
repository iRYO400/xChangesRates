package workshop.akbolatss.xchangesrates.domain.usecase

import kotlinx.coroutines.CoroutineScope
import workshop.akbolatss.xchangesrates.base.BaseUseCase
import workshop.akbolatss.xchangesrates.base.None
import workshop.akbolatss.xchangesrates.base.resource.Either
import workshop.akbolatss.xchangesrates.base.resource.Failure
import workshop.akbolatss.xchangesrates.domain.repository.ChartRepository
import workshop.akbolatss.xchangesrates.model.response.ChartData

class UpdateSingleSnapshotUseCase(
        private val repository: ChartRepository
) : BaseUseCase<UpdateSingleSnapshotUseCase.Params, None>() {

    override suspend fun run(params: Params, scope: CoroutineScope): Either<Failure, None> {
        val chartData = repository.findBy(params.itemId)
        return repository.updateChartData(chartData)
    }

    data class Params(val itemId: Long)
}
