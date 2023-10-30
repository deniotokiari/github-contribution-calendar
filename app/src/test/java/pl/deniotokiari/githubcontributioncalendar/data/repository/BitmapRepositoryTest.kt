package pl.deniotokiari.githubcontributioncalendar.data.repository

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import pl.deniotokiari.githubcontributioncalendar.core.Failed
import pl.deniotokiari.githubcontributioncalendar.core.Success
import pl.deniotokiari.githubcontributioncalendar.core.failedOrNull
import pl.deniotokiari.githubcontributioncalendar.core.successOrNull
import pl.deniotokiari.githubcontributioncalendar.data.datasource.BitmapDataSource
import pl.deniotokiari.githubcontributioncalendar.data.model.BitmapError

class BitmapRepositoryTest {
    private lateinit var bitmapDataSource: BitmapDataSource
    private lateinit var sut: AndroidBitmapRepository

    @Before
    fun setUp() {
        bitmapDataSource = mock()
        sut = AndroidBitmapRepository(bitmapDataSource)
    }

    @Test
    fun `GIVEN calculate meta data fails WHEN get bitmap from repository THEN return error`() = runTest {
        // GIVEN
        val width = 100
        val height = 100
        val blockSize = 10
        val colors = mock<List<Int>> {
            whenever(it.size).thenReturn(1000)
        }
        val colorsSize = colors.size
        val padding = 4
        val opacity = 1
        whenever(
            bitmapDataSource.calculateMetaData(
                width = width,
                height = height,
                blockSize = blockSize,
                colorsSize = colorsSize
            )
        ).thenReturn(Failed(BitmapError(mock())))

        // WHEN
        val result = sut.getBitmap(
            width = width,
            height = height,
            blockSize = blockSize,
            padding = padding,
            colors = colors,
            opacity = opacity
        )

        // THEN
        assertNotNull(result.failedOrNull())
    }

    @Test
    fun `GIVEN calculate meta data success AND get bitmap fails WHEN get bitmap from repository THEN return error`() =
        runTest {
            // GIVEN
            val width = 100
            val height = 100
            val blockSize = 10
            val colors = mock<List<Int>> {
                whenever(it.size).thenReturn(1000)
            }
            val colorsSize = colors.size
            val padding = 4
            val opacity = 1
            val metaData = BitmapDataSource.MetaData(
                hCount = 9,
                wCount = 9,
                hOffset = 5,
                wOffset = 5
            )
            whenever(
                bitmapDataSource.calculateMetaData(
                    width = width,
                    height = height,
                    blockSize = blockSize,
                    colorsSize = colorsSize
                )
            ).thenReturn(Success(metaData))
            whenever(
                bitmapDataSource.getBitmap(
                    metaData,
                    width,
                    height,
                    blockSize,
                    padding,
                    colors,
                    opacity
                )
            ).thenReturn(
                Failed(
                    BitmapError(mock())
                )
            )

            // WHEN
            val result = sut.getBitmap(
                width = width,
                height = height,
                blockSize = blockSize,
                padding = padding,
                colors = colors,
                opacity = opacity
            )

            // THEN
            assertNotNull(result.failedOrNull())
        }

    @Test
    fun `GIVEN calculate meta data success AND get bitmap success WHEN get bitmap from repository THEN return bitmap`() =
        runTest {
            // GIVEN
            val width = 100
            val height = 100
            val blockSize = 10
            val colors = mock<List<Int>> {
                whenever(it.size).thenReturn(1000)
            }
            val colorsSize = colors.size
            val padding = 4
            val opacity = 1
            val metaData = BitmapDataSource.MetaData(
                hCount = 9,
                wCount = 9,
                hOffset = 5,
                wOffset = 5
            )
            whenever(
                bitmapDataSource.calculateMetaData(
                    width = width,
                    height = height,
                    blockSize = blockSize,
                    colorsSize = colorsSize
                )
            ).thenReturn(Success(metaData))
            whenever(
                bitmapDataSource.getBitmap(
                    metaData,
                    width,
                    height,
                    blockSize,
                    padding,
                    colors,
                    opacity
                )
            ).thenReturn(
                Success(mock())
            )

            // WHEN
            val result = sut.getBitmap(
                width = width,
                height = height,
                blockSize = blockSize,
                padding = padding,
                colors = colors,
                opacity = opacity
            )

            // THEN
            assertNotNull(result.successOrNull())
        }

    @Test
    fun `GIVEN calculate meta data fails WHEN get meta data from repository THEN return error`() = runTest {
        // GIVEN
        val width = 100
        val height = 100
        val blockSize = 10
        val colorsSize = 1000
        whenever(
            bitmapDataSource.calculateMetaData(
                width = width,
                height = height,
                blockSize = blockSize,
                colorsSize = colorsSize
            )
        ).thenReturn(Failed(BitmapError(mock())))

        // WHEN
        val result = sut.getMetaData(
            width = width,
            height = height,
            blockSize = blockSize,
            colorsSize = colorsSize
        )

        // THEN
        assertNotNull(result.failedOrNull())
    }

    @Test
    fun `GIVEN calculate meta data returns success WHEN get meta data from repository THEN return meta data`() =
        runTest {
            // GIVEN
            val width = 100
            val height = 100
            val blockSize = 10
            val colorsSize = 1000
            whenever(
                bitmapDataSource.calculateMetaData(
                    width = width,
                    height = height,
                    blockSize = blockSize,
                    colorsSize = colorsSize
                )
            ).thenReturn(
                Success(
                    BitmapDataSource.MetaData(
                        hCount = 9,
                        wCount = 9,
                        hOffset = 5,
                        wOffset = 5
                    )
                )
            )

            // WHEN
            val result = sut.getMetaData(
                width = width,
                height = height,
                blockSize = blockSize,
                colorsSize = colorsSize
            )

            // THEN
            assertEquals(
                BitmapDataSource.MetaData(
                    hCount = 9,
                    wCount = 9,
                    hOffset = 5,
                    wOffset = 5
                ), result.successOrNull()
            )
        }
}