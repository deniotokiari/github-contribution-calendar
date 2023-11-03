package pl.deniotokiari.githubcontributioncalendar.data.model

@JvmInline
value class BitmapError(val throwable: Throwable)

@JvmInline
value class ContributionsError(val throwable: Throwable)

@JvmInline
value class WidgetConfigurationError(val throwable: Throwable)

@JvmInline
value class UserName(val value: String)

@JvmInline
value class Year(val value: Int)

@JvmInline
value class WidgetId(val value: Int)

@JvmInline
value class Contributions(val colors: List<String>) {
    fun toLocalModel(): String = colors.joinToString(separator = ",")

    companion object {
        fun fromLocalModel(model: Any?): Contributions = Contributions(
            (model as String).split(",")
        )
    }
}

@JvmInline
value class Padding(val value: Int)

@JvmInline
value class Opacity(val value: Int)

@JvmInline
value class BlockSize(val value: Int)

data class WidgetConfiguration(
    val padding: Padding,
    val opacity: Opacity,
    val blockSize: BlockSize
) {
    fun toLocalModel(): String = "${padding.value}:${opacity.value}:${blockSize.value}"

    companion object {
        fun fromLocalModel(model: Any?): WidgetConfiguration = model.let { it as String }.let { item ->
            item.split(":").let {
                WidgetConfiguration(
                    padding = Padding(it[0].toInt()),
                    opacity = Opacity(it[1].toInt()),
                    blockSize = BlockSize(it[2].toInt())
                )
            }
        }
    }
}