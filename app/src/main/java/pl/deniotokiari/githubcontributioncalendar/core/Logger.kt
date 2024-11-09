package pl.deniotokiari.githubcontributioncalendar.core

class Logger(
    private val onLog: (String) -> Unit,
    private val onError: (Throwable) -> Unit,
) {
    fun log(message: String) {
        onLog(message)
    }

    fun error(error: Throwable) {
        onError(error)
    }
}
