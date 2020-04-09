/**
 * Copyright (C) 2018 Fernando Cejas Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

    inline fun fold(fnL: (L) -> Unit = {}, fnR: (R) -> Any = {}): Any =
        when (this) {
            is Left -> fnL(a)
            is Right -> fnR(b)
        }
}

inline fun <L, R> Either<L, R>.onSuccess(action: (R) -> Unit): Either<L, R> {
    if (this is Right) action(b)
    return this
}

inline fun <L, R> Either<L, R>.onFailure(action: (L) -> Unit) {
    if (this is Left) {
        Timber.e("Some shit happened $a")
        action(a)
    }
}

inline fun <T, L, R> Either<L, R>.map(fn: (R) -> (T)): Either<L, T> =
    when (this) {
        is Left -> Left(a)
        is Right -> right(fn(b))
    }

inline fun <T, L, R> Either<L, R>.flatMap(fn: (R) -> Either<L, T>): Either<L, T> =
    when (this) {
        is Left -> Left(a)
        is Right -> fn(b)
    }

inline fun <L, R> Either<L, R>.log(fn: (R) -> String): Either<L, R> =
    when (this) {
        is Left -> Left(a)
        is Right -> {
            Timber.d(" ${fn(b)}")
            Right(b)
        }
    }

inline fun <L, R, Z> Either<L, R>.zipWith(fn: (R) -> Either<L, Z>): Either<L, Pair<R, Z>> =
    when (this) {
        is Left -> Left(a)
        is Right -> {
            fn(b).flatMap {
                Right(Pair(b, it))
            }
        }
    }

inline fun <L, R, Z, X> Either<L, Pair<R, Z>>.zipWithUp(fn: (Pair<R, Z>) -> Either<L, X>): Either<L, Triple<R, Z, X>> =
    when (this) {
        is Left -> Left(a)
        is Right -> {
            fn(Pair(b.first, b.second)).flatMap {
                Right(Triple(b.first, b.second, it))
            }
        }
    }

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

inline fun <L, R> Either<L, R>.onFailureReturn(failure: (L) -> Either<L, R>): Either<L, R> =
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

