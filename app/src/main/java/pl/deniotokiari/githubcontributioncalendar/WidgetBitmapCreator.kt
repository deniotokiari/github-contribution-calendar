package pl.deniotokiari.githubcontributioncalendar

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.ui.graphics.toArgb
import pl.deniotokiari.githubcontributioncalendar.ui.theme.Purple40

class WidgetBitmapCreator {
    operator fun invoke(width: Int, height: Int, squareSize: Int, padding: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(
            width,
            height,
            Bitmap.Config.ARGB_8888
        )

        val wOffset = squareSize / 2
        val hOffset = squareSize / 2
        val size = getValue(width - wOffset * 2, squareSize)
        val wCount = (width - wOffset * 2) / size
        val hCount = (height - hOffset * 2) / size
        val sizeF = size.toFloat()
        val paddingF = padding.toFloat()
        val wOffsetF = wOffset.toFloat()
        // alter hOffset to make it symmetrical on top and bottom
        val hOffsetF = ((height - hCount * size) / 2).toFloat()
        val paint = Paint().apply { color = Purple40.toArgb() }
        val rect = RectF()

        Canvas(bitmap).apply {
            repeat(hCount) { i ->
                repeat(wCount) { j ->
                    rect.apply {
                        left = sizeF * j
                        top = sizeF * i
                        right = sizeF * j + sizeF
                        bottom = sizeF * i + sizeF
                    }

                    rect.inset(paddingF, paddingF)
                    rect.offset(wOffsetF, hOffsetF)

                    drawRect(rect, paint)
                }
            }
        }

        return bitmap
    }

    fun getValue(a: Int, b: Int): Int {
        var c = b

        while (a.mod(c) > 0) {
            c--
        }

        return c
    }
}