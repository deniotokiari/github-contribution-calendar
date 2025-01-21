package pl.deniotokiari.githubcontributioncalendar.core.misc

interface Logger {
    fun message(message: String)

    fun error(error: Throwable)
}
