package workshop.akbolatss.xchangesrates.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import workshop.akbolatss.xchangesrates.base.resource.Either
import workshop.akbolatss.xchangesrates.base.resource.Failure

abstract class BaseUseCase<in Params, out Type> where Type : Any {

    abstract suspend fun run(params: Params, scope: CoroutineScope): Either<Failure, Type>

    open suspend operator fun invoke(
            scope: CoroutineScope,
            params: Params,
            onResult: (Either<Failure, Type>) -> Unit = {}
    ): Either<Failure, Type> {
        val backgroundJob = scope.async { run(params, this) }
        val job = scope.async {
            return@async try {
                backgroundJob.await()
            } catch (e: Exception) {
                Either.Left(Failure.UseCaseError)
            }
        }
        return job.await()
    }
}

class None
