package workshop.akbolatss.xchangesrates.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import timber.log.Timber
import workshop.akbolatss.xchangesrates.base.resource.Either
import workshop.akbolatss.xchangesrates.base.resource.Failure

abstract class BaseUseCase<in Params, out Type> where Type : Any {

    abstract suspend fun run(params: Params, scope: CoroutineScope): Either<Failure, Type>

    open suspend operator fun invoke(
        scope: CoroutineScope,
        params: Params
    ): Either<Failure, Type> {
        return withContext(scope.coroutineContext) {
            try {
                run(params, this)
            } catch (e: Exception) {
                Either.Left(Failure.UseCaseError)
            }
        }
    }
}

class None
