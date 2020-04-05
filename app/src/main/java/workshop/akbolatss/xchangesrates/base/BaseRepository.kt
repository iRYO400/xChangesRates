package workshop.akbolatss.xchangesrates.base

import retrofit2.Response
import timber.log.Timber
import workshop.akbolatss.xchangesrates.base.resource.Either
import workshop.akbolatss.xchangesrates.base.resource.Either.Left
import workshop.akbolatss.xchangesrates.base.resource.Either.Right
import workshop.akbolatss.xchangesrates.base.resource.Failure

open class BaseRepository {

    protected inline fun <reified T : Any, reified R : Any> apiCall(
            call: () -> Response<T>,
            mapResponse: (T) -> R
    ): Either<Failure, R> =
            try {
                val response = call.invoke()
                when (response.isSuccessful) {
                    true -> Right(mapResponse(response.body()!!))
                    false -> Left(Failure.ServerError(response.errorBody()?.string()))
                }
            } catch (exception: Throwable) {
                Timber.d(exception)
                Left(Failure.UnknownException(exception.message))
            }

    protected inline fun <reified T : Any> insert(
            map: () -> T,
            query: (T) -> Long
    ): Either<Failure, None> =
            try {
                val mapped = map()
                when (query.invoke(mapped)) {
                    -1L -> Left(Failure.DatabaseError("Insert of ${T::class.java} failed"))
                    else -> Right(None())
                }
            } catch (exception: Throwable) {
                Timber.d(exception)
                Left(Failure.UnknownException(exception.message))
            }

    protected inline fun <reified T : Any> insertList(
            map: () -> List<T>,
            query: (List<T>) -> List<Long>
    ): Either<Failure, None> =
            try {
                val mappedList = map()
                val insertedCount = query(mappedList).size
                when (mappedList.size == insertedCount) {
                    true -> Right(None())
                    false -> Left(Failure.DatabaseError("Insert list of ${T::class.java} failed"))
                }
            } catch (exception: Throwable) {
                Timber.d(exception)
                Left(Failure.UnknownException(exception.message))
            }

    protected inline fun <reified T : Any> update(
            map: () -> T,
            query: (T) -> Int
    ): Either<Failure, None> =
            try {
                val mapped = map()
                when (1 == query(mapped)) {
                    true -> Right(None())
                    false -> Left(Failure.DatabaseError("Update of ${T::class.java} failed"))
                }
            } catch (exception: Throwable) {
                Timber.d(exception)
                Left(Failure.UnknownException(exception.message))
            }

    protected inline fun <reified T : Any> updateList(
            map: () -> List<T>,
            query: (List<T>) -> Int
    ): Either<Failure, None> =
            try {
                val mappedList = map()
                val updatedCount = query(mappedList)
                when (mappedList.size == updatedCount) {
                    true -> Right(None())
                    else -> Left(Failure.DatabaseError("Update list of ${T::class.java} failed"))
                }
            } catch (exception: Throwable) {
                Timber.d(exception)
                Left(Failure.UnknownException(exception.message))
            }

    protected inline fun delete(
            query: () -> Int,
            checkRowCount: Int = 1
    ): Either<Failure, None> =
            try {
                val updatedRowCount = query()
                when (checkRowCount == updatedRowCount) {
                    true -> Right(None())
                    false -> Left(Failure.DatabaseError("Delete failed"))
                }
            } catch (exception: Throwable) {
                Timber.d(exception)
                Left(Failure.UnknownException(exception.message))
            }

}
