package pl.deniotokiari.githubcontributioncalendar.core

import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class UseCaseTest {

    @Test
    fun `UseCase interface should work with simple implementation`() = runTest {
        // Given
        val useCase = object : UseCase<String, String> {
            override suspend fun invoke(params: String): String {
                return "Hello, $params!"
            }
        }

        // When
        val result = useCase("World")

        // Then
        assertEquals("Hello, World!", result)
    }

    @Test
    fun `UseCase interface should work with different parameter types`() = runTest {
        // Given
        val useCase = object : UseCase<Int, String> {
            override suspend fun invoke(params: Int): String {
                return "Number: $params"
            }
        }

        // When
        val result = useCase(42)

        // Then
        assertEquals("Number: 42", result)
    }

    @Test
    fun `UseCase interface should work with complex parameter types`() = runTest {
        // Given
        data class TestParams(val name: String, val age: Int)
        data class TestResult(val message: String, val isValid: Boolean)

        val useCase = object : UseCase<TestParams, TestResult> {
            override suspend fun invoke(params: TestParams): TestResult {
                return TestResult(
                    message = "Name: ${params.name}, Age: ${params.age}",
                    isValid = params.age >= 18
                )
            }
        }

        // When
        val result = useCase(TestParams("John", 25))

        // Then
        assertEquals("Name: John, Age: 25", result.message)
        assertTrue(result.isValid)
    }

    @Test
    fun `FlowUseCase interface should work with flow implementation`() = runTest {
        // Given
        val useCase = object : FlowUseCase<String, String> {
            override fun invoke(params: String) = flowOf("Hello, $params!")
        }

        // When
        val result = useCase("World")

        // Then
        // FlowUseCase returns a Flow, so we can collect from it
        var collectedValue: String? = null
        result.collect { value ->
            collectedValue = value
        }
        assertEquals("Hello, World!", collectedValue)
    }

    @Test
    fun `FlowUseCase interface should work with multiple emissions`() = runTest {
        // Given
        val useCase = object : FlowUseCase<Int, String> {
            override fun invoke(params: Int) = flowOf(
                "First: $params",
                "Second: ${params * 2}",
                "Third: ${params * 3}"
            )
        }

        // When
        val result = useCase(5)

        // Then
        val values = mutableListOf<String>()
        result.collect { value ->
            values.add(value)
        }
        assertEquals(3, values.size)
        assertEquals("First: 5", values[0])
        assertEquals("Second: 10", values[1])
        assertEquals("Third: 15", values[2])
    }

    @Test
    fun `FlowUseCase interface should work with empty flow`() = runTest {
        // Given
        val useCase = object : FlowUseCase<String, String> {
            override fun invoke(params: String) = flowOf<String>()
        }

        // When
        val result = useCase("test")

        // Then
        val values = mutableListOf<String>()
        result.collect { value ->
            values.add(value)
        }
        assertTrue(values.isEmpty())
    }
}
