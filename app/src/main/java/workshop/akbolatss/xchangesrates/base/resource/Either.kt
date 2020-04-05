package workshop.akbolatss.xchangesrates.base.resource

import timber.log.Timber
import workshop.akbolatss.xchangesrates.base.resource.Either.Left
import workshop.akbolatss.xchangesrates.base.resource.Either.Right

/**
 * Represents a value of one of two possible types (a disjoint union).
 * Instances of [Either] are either an instance of [Left] or [Right].
 * FP Convention dictates that [Left] is used for "failure"
 * and [Right] is used for "success".
 *
 * @see Left
 * @see Right
 */
sealed class Either<out L, out R> {
    /** * Represents the left side of [Either] class which by convention is a "Failure". */
    data class Left<out L>(val a: L) : Either<L, Nothing>()

    /** * Represents the right side of [Either] class which by convention is a "Success". */
    data class Right<out R>(val b: R) : Either<Nothing, R>()

    val isRight get() = this is Right<R>
    val isLeft get() = this is Left<L>

    fun <L> left(a: L) = Left(a)
    fun <R> right(b: R) = Right(b)

    @Suppress("RedundantSuspendModifier")
    suspend fun <R> rightAsync(b: R) = Right(b)

    inline fun fold(fnL: (L) -> Unit = {}, fnR: (R) -> Any = {}): Any =
        when (this) {
            is Left -> fnL(a)
            is Right -> fnR(b)
        }
}

// Credits to Alex Hart -> https://proandroiddev.com/kotlins-nothing-type-946de7d464fb
// Composes 2 functions
//fun <A, B, C> ((A) -> B).c(f: (B) -> C): (A) -> C = {
//    f(this(it))
//}
//
//inline fun <T, L, R> Either<L, R>.flatMapSync(fn: (R) -> Either<L, T>): Either<L, T> =
//    when (this) {
//        is Left -> Left(a)
//        is Right -> fn(b)
//    }
//
//fun <T, L, R> Either<L, R>.mapSync(fn: (R) -> (T)): Either<L, T> = this.flatMapSync(fn.c(::right))


suspend fun <A, B, C> (suspend (A) -> B).c(f: suspend (B) -> C): suspend (A) -> C = {
    f(this(it))
}

fun <L, R> Either<L, R>.log(message: String): Either<L, R> =
    when (this) {
        is Left -> Left(a)
        is Right -> {
            Timber.d("%s %s", message, b.toString())
            Right(b)
        }
    }


suspend fun <T, L, R> Either<L, R>.flatMap(fn: suspend (R) -> Either<L, T>): Either<L, T> =
    when (this) {
        is Left -> Left(a)
        is Right -> fn(b)
    }

suspend fun <T, L, R> Either<L, R>.map(fn: suspend (R) -> (T)): Either<L, T> =
    this.flatMap(fn.c(::rightAsync))


suspend fun <L, R, Z> Either<L, R>.zipWith(fn: suspend (R) -> Either<L, Z>): Either<L, Pair<R, Z>> =
    when (this) {
        is Left -> Left(a)
        is Right -> {
            fn(b).flatMap {
                Right(Pair(b, it))
            }
        }
    }

suspend fun <L, R, Z, X> Either<L, Pair<R, Z>>.zipWithUp(fn: suspend (Pair<R, Z>) -> Either<L, X>): Either<L, Triple<R, Z, X>> =
    when (this) {
        is Left -> Left(a)
        is Right -> {
            fn(Pair(b.first, b.second)).flatMap {
                Right(Triple(b.first, b.second, it))
            }
        }
    }

//suspend fun <L, R, Z> Either<L, R>.zipWith(toZip: Z): Either<L, Pair<R, Z>> =
//    when (this) {
//        is Left -> Left(a)
//        is Right -> Right(Pair(b, toZip))
//    }

inline fun <L, R> Either<L, R>.doOnFailure(failure: (L) -> Unit): Either<L, R> =
    when (this) {
        is Left -> {
            failure.invoke(a)
            Left(a)
        }
        is Right -> Right(b)
    }

inline fun <T, L, R> Either<L, R>.onFailureResumeNext(failure: (L) -> Either<T, R>): Either<T, R> =
    when (this) {
        is Left -> failure(a)
        is Right -> Right(b)
    }

inline fun <L, R> Either<L, R>.unwrapResponseOrThrow(failure: (L) -> Throwable): R =
    when (this) {
        is Left -> throw failure(a)
        is Right -> b
    }

fun <R> Either<Failure, R>.mapFailure(featureFailure: Failure): Either<Failure, R> =
    when (this) {
        is Left -> {
            featureFailure.errorMessage = a.errorMessage
            Left(featureFailure)
        }
        is Right -> Right(b)
    }

fun <R, C : MutableCollection<R>> Sequence<R>.toRightCollection(destination: C): Either<Failure, C> =
    Right(this.toCollection(destination))

