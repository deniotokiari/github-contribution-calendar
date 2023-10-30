package pl.deniotokiari.githubcontributioncalendar.data.repository

import android.graphics.Bitmap
import pl.deniotokiari.githubcontributioncalendar.core.Result
import pl.deniotokiari.githubcontributioncalendar.core.flatMap
import pl.deniotokiari.githubcontributioncalendar.data.datasource.BitmapDataSource
import pl.deniotokiari.githubcontributioncalendar.data.model.BitmapError

interface BitmapRepository {
    fun getMetaData(
        width: Int,
        height: Int,
        blockSize: Int,
        colorsSize: Int
    ): Result<BitmapDataSource.MetaData, BitmapError>

    fun getBitmap(
        width: Int,
        height: Int,
        blockSize: Int,
        padding: Int,
        colors: List<Int>,
        opacity: Int
    ): Result<Bitmap, BitmapError>
}

class AndroidBitmapRepository(
    private val bitmapDataSource: BitmapDataSource
) : BitmapRepository {
    override fun getMetaData(
        width: Int,
        height: Int,
        blockSize: Int,
        colorsSize: Int
    ): Result<BitmapDataSource.MetaData, BitmapError> = bitmapDataSource.calculateMetaData(
        width = width,
        height = height,
        blockSize = blockSize,
        colorsSize = colorsSize
    )

    override fun getBitmap(
        width: Int,
        height: Int,
        blockSize: Int,
        padding: Int,
        colors: List<Int>,
        opacity: Int
    ): Result<Bitmap, BitmapError> = bitmapDataSource.calculateMetaData(
        width = width,
        height = height,
        blockSize = blockSize,
        colorsSize = colors.size
    ).flatMap { metaData ->
        bitmapDataSource.getBitmap(
            metaData = metaData,
            width = width,
            height = height,
            blockSize = blockSize,
            padding = padding,
            colors = colors,
            opacity = opacity
        )
    }
}