package pl.deniotokiari.githubcontributioncalendar.data.model

import org.junit.Assert.*
import org.junit.Test

class ModelsTest {

    @Test
    fun `UserName should wrap string value`() {
        // Given
        val username = "testuser"

        // When
        val userName = UserName(username)

        // Then
        assertEquals(username, userName.value)
    }

    @Test
    fun `Year should wrap int value`() {
        // Given
        val year = 2024

        // When
        val yearValue = Year(year)

        // Then
        assertEquals(year, yearValue.value)
    }

    @Test
    fun `WidgetId should wrap int value`() {
        // Given
        val id = 123

        // When
        val widgetId = WidgetId(id)

        // Then
        assertEquals(id, widgetId.value)
    }

    @Test
    fun `Contributions isEmpty should return true for empty list`() {
        // Given
        val contributions = Contributions(emptyList())

        // When
        val isEmpty = contributions.isEmpty()

        // Then
        assertTrue(isEmpty)
    }

    @Test
    fun `Contributions isEmpty should return false for non-empty list`() {
        // Given
        val contributions = Contributions(listOf("#000000", "#ffffff"))

        // When
        val isEmpty = contributions.isEmpty()

        // Then
        assertFalse(isEmpty)
    }


    @Test
    fun `Contributions toLocalModel should join colors with comma`() {
        // Given
        val contributions = Contributions(listOf("#000000", "#ffffff", "#ff0000"))

        // When
        val localModel = contributions.toLocalModel()

        // Then
        assertEquals("#000000,#ffffff,#ff0000", localModel)
    }

    @Test
    fun `Contributions fromLocalModel should split comma-separated colors`() {
        // Given
        val localModel = "#000000,#ffffff,#ff0000"

        // When
        val contributions = Contributions.fromLocalModel(localModel)

        // Then
        assertEquals(listOf("#000000", "#ffffff", "#ff0000"), contributions.colors)
    }

    @Test
    fun `Padding should wrap int value`() {
        // Given
        val padding = 10

        // When
        val paddingValue = Padding(padding)

        // Then
        assertEquals(padding, paddingValue.value)
    }

    @Test
    fun `Opacity should wrap int value`() {
        // Given
        val opacity = 80

        // When
        val opacityValue = Opacity(opacity)

        // Then
        assertEquals(opacity, opacityValue.value)
    }

    @Test
    fun `BlockSize should wrap int value`() {
        // Given
        val blockSize = 30

        // When
        val blockSizeValue = BlockSize(blockSize)

        // Then
        assertEquals(blockSize, blockSizeValue.value)
    }

    @Test
    fun `WidgetConfiguration should contain all properties`() {
        // Given
        val padding = Padding(10)
        val opacity = Opacity(80)
        val blockSize = BlockSize(30)

        // When
        val config = WidgetConfiguration(padding, opacity, blockSize)

        // Then
        assertEquals(padding, config.padding)
        assertEquals(opacity, config.opacity)
        assertEquals(blockSize, config.blockSize)
    }

    @Test
    fun `WidgetConfiguration toLocalModel should format correctly`() {
        // Given
        val config = WidgetConfiguration(
            padding = Padding(10),
            opacity = Opacity(80),
            blockSize = BlockSize(30)
        )

        // When
        val localModel = config.toLocalModel()

        // Then
        assertEquals("10:80:30", localModel)
    }

    @Test
    fun `WidgetConfiguration fromLocalModel should parse correctly`() {
        // Given
        val localModel = "10:80:30"

        // When
        val config = WidgetConfiguration.fromLocalModel(localModel)

        // Then
        assertEquals(Padding(10), config.padding)
        assertEquals(Opacity(80), config.opacity)
        assertEquals(BlockSize(30), config.blockSize)
    }

    @Test
    fun `WidgetConfiguration default should have correct values`() {
        // When
        val config = WidgetConfiguration.default()

        // Then
        assertEquals(Padding(2), config.padding)
        assertEquals(Opacity(255), config.opacity)
        assertEquals(BlockSize(40), config.blockSize)
    }

    @Test
    fun `WidgetConfiguration constants should have correct values`() {
        // Then
        assertEquals(20, WidgetConfiguration.BLOCK_SIZE_MIN)
        assertEquals(60, WidgetConfiguration.BLOCK_SIZE_MAX)
        assertEquals(90, WidgetConfiguration.OPACITY_MIN)
        assertEquals(255, WidgetConfiguration.OPACITY_MAX)
        assertEquals(0, WidgetConfiguration.PADDING_MIN)
        assertEquals(4, WidgetConfiguration.PADDING_MAX)
    }
}