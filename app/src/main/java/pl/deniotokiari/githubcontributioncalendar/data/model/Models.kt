package pl.deniotokiari.githubcontributioncalendar.data.model

@JvmInline
value class BitmapError(val throwable: Throwable)

@JvmInline
value class ContributionsError(val throwable: Throwable)

@JvmInline
value class Contributions(val colors: List<String>) {
    fun toLocalModel(): String = colors.joinToString(separator = ",")

    companion object {
        fun fromLocalModel(model: Any?): Contributions = Contributions(
            (model as? String)?.split(",") ?: emptyList()
        )
    }
}