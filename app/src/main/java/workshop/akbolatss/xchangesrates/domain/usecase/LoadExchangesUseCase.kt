package workshop.akbolatss.xchangesrates.domain.usecase

import kotlinx.coroutines.CoroutineScope
import workshop.akbolatss.xchangesrates.base.BaseUseCase
import workshop.akbolatss.xchangesrates.base.None
import workshop.akbolatss.xchangesrates.base.resource.Either
import workshop.akbolatss.xchangesrates.base.resource.Failure
import workshop.akbolatss.xchangesrates.domain.model.Exchange
import workshop.akbolatss.xchangesrates.domain.repository.ExchangeRepository

class LoadExchangesUseCase(
    private val repository: ExchangeRepository
) : BaseUseCase<LoadExchangesUseCase.Params, List<Exchange>>() {

    override suspend fun run(
        params: Params,
        scope: CoroutineScope
    ): Either<Failure, List<Exchange>> {
        val exchanges = repository.findAll().sortedBy { exchange ->
            exchange.caption
        }
        return Either.Right(exchanges)
    }

    data class Params(val none: None = None())
}
