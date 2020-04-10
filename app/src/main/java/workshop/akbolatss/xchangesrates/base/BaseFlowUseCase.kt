package workshop.akbolatss.xchangesrates.base

import kotlinx.coroutines.flow.Flow

abstract class BaseFlowUseCase<in Params, out Type> where Type : Any {

    abstract fun run(params: Params): Flow<Type>

    open operator fun invoke(
        params: Params
    ): Flow<Type> = run(params)

}
