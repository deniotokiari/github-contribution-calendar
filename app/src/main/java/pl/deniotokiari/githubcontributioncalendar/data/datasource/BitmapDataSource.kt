package pl.deniotokiari.githubcontributioncalendar.data.datasource

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import pl.deniotokiari.githubcontributioncalendar.core.Result
import pl.deniotokiari.githubcontributioncalendar.core.asFailed
import pl.deniotokiari.githubcontributioncalendar.core.success
import pl.deniotokiari.githubcontributioncalendar.data.model.DataError
import java.lang.Integer.max
import java.lang.Integer.min

interface BitmapDataSource {
    fun getBitmap(
        metaData: MetaData,
        width: Int,
        height: Int,
        blockSize: Int,
        padding: Int,
        colors: List<Int>,
        opacity: Int
    ): Result<Bitmap, DataError>

    fun calculateMetaData(
        width: Int,
        height: Int,
        blockSize: Int,
        colorsSize: Int
    ): Result<MetaData, DataError>

    data class MetaData(
        val hCount: Int,
        val wCount: Int,
        val hOffset: Int,
        val wOffset: Int
    ) {
        val blocksCount: Int = hCount * wCount
    }
}

class AndroidBitmapDataSource : BitmapDataSource {
    override fun getBitmap(
        metaData: BitmapDataSource.MetaData,
        width: Int,
        height: Int,
        blockSize: Int,
        padding: Int,
        colors: List<Int>,
        opacity: Int
    ): Result<Bitmap, DataError> = runCatching {
        val bitmap = Bitmap.createBitmap(
            width,
            height,
            Bitmap.Config.ARGB_8888
        )

        if (metaData.hCount <= 0 || metaData.wCount <= 0) {
            return@runCatching bitmap
        }

        val paint = Paint()
        val rect = RectF()
        val blockSizeF = blockSize.toFloat()
        val paddingF = padding.toFloat()
        val hOffsetF = metaData.hOffset.toFloat()
        val wOffsetF = metaData.wOffset.toFloat()
        var index = 0

        Canvas(bitmap).apply {
            repeat(metaData.hCount) { i ->
                val iOffset = i * blockSizeF

                repeat(metaData.wCount) { j ->
                    val jOffset = j * blockSizeF

                    rect.apply {
                        left = jOffset
                        top = iOffset
                        right = jOffset + blockSize
                        bottom = iOffset + blockSize

                        inset(paddingF, paddingF)
                        offset(wOffsetF, hOffsetF)
                    }

                    paint.apply {
                        color = colors[index++]
                        alpha = opacity
                    }

                    drawRect(rect, paint)
                }
            }
        }

        bitmap
    }.fold(
        onSuccess = { it.success() },
        onFailure = { it.asFailed(::DataError) }
    )

    override fun calculateMetaData(
        width: Int,
        height: Int,
        blockSize: Int,
        colorsSize: Int
    ): Result<BitmapDataSource.MetaData, DataError> = runCatching {
        if (width <= 0 || height <= 0 || blockSize <= 0 || colorsSize <= 0) {
            return@runCatching BitmapDataSource.MetaData(
                hCount = 0,
                wCount = 0,
                hOffset = 0,
                wOffset = 0
            )
        }

        var wOffset = blockSize / 2
        var hOffset = blockSize / 2
        val wCount = min(((width - wOffset * 2) / blockSize), colorsSize)
        val hCountMax = max(colorsSize / wCount, 1)
        val hCount = min((height - hOffset * 2) / blockSize, hCountMax)
        // alter hOffset to make it symmetrical on top and bottom
        hOffset = (height - hCount * blockSize) / 2
        // alter wOffset to make it symmetrical on left and right
        wOffset = (width - wCount * blockSize) / 2

        BitmapDataSource.MetaData(
            hCount = hCount,
            wCount = wCount,
            hOffset = hOffset,
            wOffset = wOffset
        )
    }.fold(
        onSuccess = { it.success() },
        onFailure = { it.asFailed(::DataError) }
    )
}