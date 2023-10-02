package pl.deniotokiari.githubcontributioncalendar

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.ui.graphics.toArgb
import pl.deniotokiari.githubcontributioncalendar.ui.theme.Purple40

class Repo {
    fun getBitmap(width: Int, height: Int): Bitmap {
        val list = mutableListOf<Int>()

        repeat(width * height) {
            list.add(Color.TRANSPARENT)
        }

        val b = Bitmap.createBitmap(
            intArrayOf(*list.toIntArray()),
            width,
            height,
            Bitmap.Config.ARGB_8888
        ).copy(Bitmap.Config.ARGB_8888, true)

        val square = 20
        val offset = square / 2
        val size = getValue(width, square)
        val padding = 4.dp.toFloat()
        val w_count = (width - offset) / size
        val h_count = (height - offset) / size
        val size_f = size.toFloat()

        Canvas(b).apply {
            val paint = Paint()

            paint.color = Purple40.toArgb()

            repeat(h_count) { i ->
                repeat(w_count) { j ->
                    val rect = RectF(
                        size_f * j,
                        size_f * i,
                        size_f * j + size_f,
                        size_f * i + size_f
                    )

                    rect.inset(padding, padding)
                    rect.offset(offset.toFloat(), offset.toFloat())

                    drawRect(rect, paint)
                }
            }
        }

        return b
    }

    fun getValue(a: Int, b: Int): Int {
        var c = b

        while (a.mod(c) > 0) {
            c--
        }

        return c
    }
}

private val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
private val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()