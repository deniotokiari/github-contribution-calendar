package pl.deniotokiari.githubcontributioncalendar.data.model

import android.graphics.Color

@JvmInline
value class BitmapError(val throwable: Throwable)

@JvmInline
value class ContributionsError(val throwable: Throwable)

@JvmInline
value class WidgetConfigurationError(val throwable: Throwable)

@JvmInline
value class WidgetError(val throwable: Throwable)

@JvmInline
value class AppConfigurationError(val throwable: Throwable)

@JvmInline
value class UserName(val value: String)

@JvmInline
value class Year(val value: Int)

@JvmInline
value class WidgetId(val value: Int)

@JvmInline
value class Contributions(val colors: List<String>) {
    fun isEmpty(): Boolean = colors.isEmpty()

    fun asIntColors(): List<Int> = colors.map(Color::parseColor)

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
        const val BLOCK_SIZE_MIN = 20
        const val BLOCK_SIZE_MAX = 60

        const val PADDING_MIN = 0
        const val PADDING_MAX = 4

        const val OPACITY_MIN = 90
        const val OPACITY_MAX = 255

        const val DEFAULT_BLOCK_SIZE = 40
        const val DEFAULT_PADDING = 2
        const val DEFAULT_OPACITY = 255

        fun fromLocalModel(model: Any?): WidgetConfiguration = model.let { it as String }.let { item ->
            item.split(":").let {
                WidgetConfiguration(
                    padding = Padding(it[0].toInt()),
                    opacity = Opacity(it[1].toInt()),
                    blockSize = BlockSize(it[2].toInt())
                )
            }
        }

        fun default(): WidgetConfiguration = WidgetConfiguration(
            padding = Padding(DEFAULT_PADDING),
            opacity = Opacity(DEFAULT_OPACITY),
            blockSize = BlockSize(DEFAULT_BLOCK_SIZE)
        )
    }
}