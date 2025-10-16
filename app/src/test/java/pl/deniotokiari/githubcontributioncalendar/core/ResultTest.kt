package pl.deniotokiari.githubcontributioncalendar.core

import org.junit.Assert.*
import org.junit.Test

class ResultTest {

    @Test
    fun `success should create Success result`() {
        // Given
        val value = "test"

        // When
        val result = value.success()

        // Then
        assertTrue(result is Success)
        assertEquals(value, result.value)
    }

    @Test
    fun `failed should create Failed result`() {
        // Given
        val error = "error"

        // When
        val result = error.failed()

        // Then
        assertTrue(result is Failed)
        assertEquals(error, result.value)
    }

    @Test
    fun `fold should call success function for Success result`() {
        // Given
        val value = "test"
        val result: Result<String, String> = value.success()

        // When
        val folded = result.fold(
            success = { "success: $it" },
            failed = { "failed: $it" }
        )

        // Then
        assertEquals("success: test", folded)
    }

    @Test
    fun `fold should call failed function for Failed result`() {
        // Given
        val error = "error"
        val result: Result<String, String> = error.failed()

        // When
        val folded = result.fold(
            success = { "success: $it" },
            failed = { "failed: $it" }
        )

        // Then
        assertEquals("failed: error", folded)
    }

    @Test
    fun `flatMap should transform Success result`() {
        // Given
        val value = 5
        val result: Result<Int, String> = value.success()

        // When
        val mapped = result.flatMap { Success(it * 2) }

        // Then
        assertTrue(mapped is Success)
        assertEquals(10, (mapped as Success).value)
    }

    @Test
    fun `flatMap should return Failed result unchanged`() {
        // Given
        val error = "error"
        val result: Result<Int, String> = error.failed()

        // When
        val mapped = result.flatMap { Success(it * 2) }

        // Then
        assertTrue(mapped is Failed)
        assertEquals(error, (mapped as Failed).value)
    }

    @Test
    fun `mapSuccess should transform Success result`() {
        // Given
        val value = 5
        val result: Result<Int, String> = value.success()

        // When
        val mapped = result.mapSuccess { it * 2 }

        // Then
        assertTrue(mapped is Success)
        assertEquals(10, (mapped as Success).value)
    }

    @Test
    fun `mapSuccess should return Failed result unchanged`() {
        // Given
        val error = "error"
        val result: Result<Int, String> = error.failed()

        // When
        val mapped = result.mapSuccess { it * 2 }

        // Then
        assertTrue(mapped is Failed)
        assertEquals(error, (mapped as Failed).value)
    }

    @Test
    fun `mapFailure should transform Failed result`() {
        // Given
        val error = "error"
        val result: Result<Int, String> = error.failed()

        // When
        val mapped = result.mapFailure { "transformed: $it" }

        // Then
        assertTrue(mapped is Failed)
        assertEquals("transformed: error", (mapped as Failed).value)
    }

    @Test
    fun `mapFailure should return Success result unchanged`() {
        // Given
        val value = 5
        val result: Result<Int, String> = value.success()

        // When
        val mapped = result.mapFailure { "transformed: $it" }

        // Then
        assertTrue(mapped is Success)
        assertEquals(value, (mapped as Success).value)
    }

    @Test
    fun `successOrNull should return value for Success result`() {
        // Given
        val value = "test"
        val result: Result<String, String> = value.success()

        // When
        val extracted = result.successOrNull()

        // Then
        assertEquals(value, extracted)
    }

    @Test
    fun `successOrNull should return null for Failed result`() {
        // Given
        val error = "error"
        val result: Result<String, String> = error.failed()

        // When
        val extracted = result.successOrNull()

        // Then
        assertNull(extracted)
    }

    @Test
    fun `failedOrNull should return error for Failed result`() {
        // Given
        val error = "error"
        val result: Result<String, String> = error.failed()

        // When
        val extracted = result.failedOrNull()

        // Then
        assertEquals(error, extracted)
    }

    @Test
    fun `failedOrNull should return null for Success result`() {
        // Given
        val value = "test"
        val result: Result<String, String> = value.success()

        // When
        val extracted = result.failedOrNull()

        // Then
        assertNull(extracted)
    }

    @Test
    fun `combineResult should combine two Success results`() {
        // Given
        val result1: Result<Int, String> = 5.success()
        val result2: Result<String, String> = "test".success()

        // When
        val combined = combineResult(result1, result2) { num, str -> "$str: $num" }

        // Then
        assertTrue(combined is Success)
        assertEquals("test: 5", (combined as Success).value)
    }

    @Test
    fun `combineResult should return Failed if first result is Failed`() {
        // Given
        val result1: Result<Int, String> = "error1".failed()
        val result2: Result<String, String> = "test".success()

        // When
        val combined = combineResult(result1, result2) { num, str -> "$str: $num" }

        // Then
        assertTrue(combined is Failed)
        assertEquals("error1", (combined as Failed).value)
    }

    @Test
    fun `combineResult should return Failed if second result is Failed`() {
        // Given
        val result1: Result<Int, String> = 5.success()
        val result2: Result<String, String> = "error2".failed()

        // When
        val combined = combineResult(result1, result2) { num, str -> "$str: $num" }

        // Then
        assertTrue(combined is Failed)
        assertEquals("error2", (combined as Failed).value)
    }

    @Test
    fun `asFailed should create Failed result from Throwable`() {
        // Given
        val throwable = RuntimeException("test error")

        // When
        val result = throwable.asFailed { pl.deniotokiari.githubcontributioncalendar.domain.model.DomainError(it) }

        // Then
        assertTrue(result is Failed)
        assertTrue((result as Failed).value is pl.deniotokiari.githubcontributioncalendar.domain.model.DomainError)
        assertEquals(throwable, (result.value as pl.deniotokiari.githubcontributioncalendar.domain.model.DomainError).throwable)
    }
}