package pl.deniotokiari.githubcontributioncalendar.core

import pl.deniotokiari.githubcontributioncalendar.R

sealed interface Result<out V, out E>

data class Success<out V>(val value: V) : Result<V, Nothing>

data class Failed<out E>(val value: E) : Result<Nothing, E>

inline fun <V, E, R> Result<V, E>.fold(
    success: (value: V) -> R,
    failed: (value: E) -> R
): R = if (this is Success) {
    success(value)
} else {
    failed((this as Failed).value)
}

inline fun <V, E, R> Result<V, E>.flatMap(next: (V) -> Result<R, E>): Result<R, E> = if (this is Success) {
    next(value)
} else {
    this as Failed
}

inline fun <V, E, R> Result<V, E>.mapSuccess(block: (V) -> R): Result<R, E> = if (this is Success) {
    Success(block(value))
} else {
    this as Failed
}

inline fun <V, E, R> Result<V, E>.mapFailure(block: (E) -> R): Result<V, R> = if (this is Failed) {
    Failed(block(value))
} else {
    this as Success
}

fun <V, E> Result<V, E>.successOrNull(): V? = (this as? Success)?.value

fun <V, E> Result<V, E>.failedOrNull(): E? = (this as? Failed)?.value

fun <R, V1, V2, E> combineResult(a: Result<V1, E>, b: Result<V2, E>, transform: (V1, V2) -> R): Result<R, E> =
    a.flatMap { r0 -> b.mapSuccess { r1 -> transform(r0, r1) } }

fun <V> V.success(): Success<V> = Success(this)

fun <V> V.failed(): Failed<V> = Failed(this)

fun <P> Throwable.asFailed(constructor: (Throwable) -> P): Failed<P> = Failed(constructor(this))
