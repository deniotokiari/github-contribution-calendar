package pl.deniotokiari.githubcontributioncalendar.data.datasource

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import pl.deniotokiari.githubcontributioncalendar.core.successOrNull

class BitmapDataSourceTest {
    private lateinit var sut: AndroidBitmapDataSource

    @Before
    fun setUp() {
        sut = AndroidBitmapDataSource()
    }

    @Test
    fun `GIVEN width = 100 AND height = 100 AND blockSize = 10 AND colorsSize = 1000 WHEN THEN`() = runTest {
        // GIVEN
        val width: Int = 100
        val height: Int = 100
        val blockSize: Int = 10
        val colorsSize: Int = 1000

        // WHEN
        val result = sut.calculateMetaData(
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

    @Test
    fun `GIVEN width = 0 WHEN calculate meta data THEN then return meta data with zero values`() = runTest {
        // GIVEN
        val width: Int = 0
        val height: Int = 100
        val blockSize: Int = 10
        val colorsSize: Int = 1000

        // WHEN
        val result = sut.calculateMetaData(
            width = width,
            height = height,
            blockSize = blockSize,
            colorsSize = colorsSize
        )

        // THEN
        assertEquals(
            BitmapDataSource.MetaData(
                hCount = 0,
                wCount = 0,
                hOffset = 0,
                wOffset = 0
            ),
            result.successOrNull()
        )
    }

    @Test
    fun `GIVEN height = 0 WHEN calculate meta data THEN then return meta data with zero values`() = runTest {
        // GIVEN
        val width: Int = 100
        val height: Int = 0
        val blockSize: Int = 10
        val colorsSize: Int = 1000

        // WHEN
        val result = sut.calculateMetaData(
            width = width,
            height = height,
            blockSize = blockSize,
            colorsSize = colorsSize
        )

        // THEN
        assertEquals(
            BitmapDataSource.MetaData(
                hCount = 0,
                wCount = 0,
                hOffset = 0,
                wOffset = 0
            ),
            result.successOrNull()
        )
    }

    @Test
    fun `GIVEN blockSize = 0 WHEN calculate meta data THEN then return meta data with zero values`() = runTest {
        // GIVEN
        val width: Int = 100
        val height: Int = 100
        val blockSize: Int = 0
        val colorsSize: Int = 1000

        // WHEN
        val result = sut.calculateMetaData(
            width = width,
            height = height,
            blockSize = blockSize,
            colorsSize = colorsSize
        )

        // THEN
        assertEquals(
            BitmapDataSource.MetaData(
                hCount = 0,
                wCount = 0,
                hOffset = 0,
                wOffset = 0
            ),
            result.successOrNull()
        )
    }

    @Test
    fun `GIVEN colorsSize = 0 WHEN calculate meta data THEN then return meta data with zero values`() = runTest {
        // GIVEN
        val width: Int = 100
        val height: Int = 100
        val blockSize: Int = 10
        val colorsSize: Int = 0

        // WHEN
        val result = sut.calculateMetaData(
            width = width,
            height = height,
            blockSize = blockSize,
            colorsSize = colorsSize
        )

        // THEN
        assertEquals(
            BitmapDataSource.MetaData(
                hCount = 0,
                wCount = 0,
                hOffset = 0,
                wOffset = 0
            ),
            result.successOrNull()
        )
    }
}