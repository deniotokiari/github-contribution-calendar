package pl.deniotokiari.githubcontributioncalendar.core

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
