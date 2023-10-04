package pl.deniotokiari.githubcontributioncalendar.etc

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import java.lang.Integer.min

class BlocksBitmapCreator {
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
        padding: Int,
        colorsSize: Int
    ): Params {
        val wOffset = squareSize / 2
        val hOffset = squareSize / 2
        val size = getValue(width - wOffset * 2, squareSize)
        val wCount = (width - wOffset * 2) / size
        val hCountMax = colorsSize / wCount
        val hCount = min((height - hOffset * 2) / size, hCountMax)
        val sizeF = size.toFloat()
        val paddingF = padding.toFloat()
        val wOffsetF = wOffset.toFloat()
        // alter hOffset to make it symmetrical on top and bottom
        val hOffsetF = ((height - hCount * size) / 2).toFloat()

        return Params(
            hCount = hCount,
            wCount = wCount,
            blockSize = sizeF,
            padding = paddingF,
            hOffset = hOffsetF,
            wOffset = wOffsetF
        )
    }

    private fun getValue(a: Int, b: Int): Int {
        var c = b

        while (a.mod(c) > 0) {
            c--
        }

        return c
    }
}