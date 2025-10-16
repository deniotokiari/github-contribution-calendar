package pl.deniotokiari.githubcontributioncalendar.core

import org.junit.Assert.*
import org.junit.Test

class IntExtensionTest {

    @Test
    fun `coerceIn should return value when within range`() {
        // Given
        val value = 5
        val min = 0
        val max = 10

        // When
        val result = value.coerceIn(min, max)

        // Then
        assertEquals(5, result)
    }

    @Test
    fun `coerceIn should return min when value is below minimum`() {
        // Given
        val value = -5
        val min = 0
        val max = 10

        // When
        val result = value.coerceIn(min, max)

        // Then
        assertEquals(0, result)
    }

    @Test
    fun `coerceIn should return max when value is above maximum`() {
        // Given
        val value = 15
        val min = 0
        val max = 10

        // When
        val result = value.coerceIn(min, max)

        // Then
        assertEquals(10, result)
    }

    @Test
    fun `coerceIn should return min when value equals minimum`() {
        // Given
        val value = 0
        val min = 0
        val max = 10

        // When
        val result = value.coerceIn(min, max)

        // Then
        assertEquals(0, result)
    }

    @Test
    fun `coerceIn should return max when value equals maximum`() {
        // Given
        val value = 10
        val min = 0
        val max = 10

        // When
        val result = value.coerceIn(min, max)

        // Then
        assertEquals(10, result)
    }

    @Test
    fun `coerceIn should handle negative range`() {
        // Given
        val value = -3
        val min = -10
        val max = -1

        // When
        val result = value.coerceIn(min, max)

        // Then
        assertEquals(-3, result)
    }

    @Test
    fun `coerceIn should handle value below negative range`() {
        // Given
        val value = -15
        val min = -10
        val max = -1

        // When
        val result = value.coerceIn(min, max)

        // Then
        assertEquals(-10, result)
    }

    @Test
    fun `coerceIn should handle value above negative range`() {
        // Given
        val value = 5
        val min = -10
        val max = -1

        // When
        val result = value.coerceIn(min, max)

        // Then
        assertEquals(-1, result)
    }

    @Test
    fun `coerceIn should handle zero range`() {
        // Given
        val value = 5
        val min = 0
        val max = 0

        // When
        val result = value.coerceIn(min, max)

        // Then
        assertEquals(0, result)
    }

    @Test
    fun `coerceIn should handle same min and max values`() {
        // Given
        val value = 5
        val min = 5
        val max = 5

        // When
        val result = value.coerceIn(min, max)

        // Then
        assertEquals(5, result)
    }

    @Test
    fun `coerceIn should handle large values`() {
        // Given
        val value = 1000000
        val min = 0
        val max = 100

        // When
        val result = value.coerceIn(min, max)

        // Then
        assertEquals(100, result)
    }

    @Test
    fun `coerceIn should handle very small values`() {
        // Given
        val value = -1000000
        val min = 0
        val max = 100

        // When
        val result = value.coerceIn(min, max)

        // Then
        assertEquals(0, result)
    }
}