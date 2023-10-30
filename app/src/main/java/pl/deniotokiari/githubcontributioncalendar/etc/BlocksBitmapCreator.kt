package pl.deniotokiari.githubcontributioncalendar.etc

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import java.lang.Integer.max
import java.lang.Integer.min

class BlocksBitmapCreator {
    operator fun invoke(
        width: Int,
        height: Int,
        squareSize: Int,
        opacity: Int,
        padding: Int,
        colors: IntArray
    ): Bitmap {
        val params = getParamsForBitmap(
            width = width,
            height = height,
            squareSize = squareSize,
            padding = padding,
            colorsSize = colors.size,
            opacity = opacity
        )

        val blocksCount = params.blocksCount
        val offset = colors.size - blocksCount

        return invoke(
            width = width,
            height = height,
            params = params,
            colors = IntArray(blocksCount) { colors[it + offset] }
        )
    }

    operator fun invoke(
        width: Int,
        height: Int,
        params: Params,
        colors: IntArray
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(
            width,
            height,
            Bitmap.Config.ARGB_8888
        )

        if (params.hCount <= 0 || params.wCount <= 0) {
            return bitmap
        }

        val paint = Paint()
        val rect = RectF()
        var index = 0

        Canvas(bitmap).apply {
            repeat(params.hCount) { i ->
                val iOffset = params.blockSize * i

                repeat(params.wCount) { j ->
                    val jOffset = params.blockSize * j

                    rect.apply {
                        left = jOffset
                        top = iOffset
                        right = jOffset + params.blockSize
                        bottom = iOffset + params.blockSize
                    }

                    rect.inset(params.padding, params.padding)
                    rect.offset(params.wOffset, params.hOffset)

                    paint.color = colors[index++]
                    paint.alpha = params.opacity
                    drawRect(rect, paint)
                }
            }
        }

        return bitmap
    }

    data class Params(
        val hCount: Int,
        val wCount: Int,
        val blockSize: Float,
        val opacity: Int,
        val padding: Float,
        val hOffset: Float,
        val wOffset: Float
    ) {
        val blocksCount: Int
            get() = hCount * wCount
    }

    fun getParamsForBitmap(
        width: Int,
        height: Int,
        squareSize: Int,
        opacity: Int,
        padding: Int,
        colorsSize: Int
    ): Params {
        val wOffset = squareSize / 2
        val hOffset = squareSize / 2
        val wCount = min(((width - wOffset * 2) / squareSize), colorsSize)
        val hCountMax = max(colorsSize / wCount, 1)
        val hCount = min((height - hOffset * 2) / squareSize, hCountMax)
        val sizeF = squareSize.toFloat()
        val paddingF = padding.toFloat()
        // alter hOffset to make it symmetrical on top and bottom
        val hOffsetF = ((height - hCount * squareSize) / 2).toFloat()
        // alter wOffset to make it symmetrical on left and right
        val wOffsetF = ((width - wCount * squareSize) / 2).toFloat()

        return Params(
            hCount = hCount,
            wCount = wCount,
            blockSize = sizeF,
            padding = paddingF,
            hOffset = hOffsetF,
            wOffset = wOffsetF,
            opacity = opacity
        )
    }

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
    }
}