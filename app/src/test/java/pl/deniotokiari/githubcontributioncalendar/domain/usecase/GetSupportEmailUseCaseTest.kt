package pl.deniotokiari.githubcontributioncalendar.domain.usecase

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import pl.deniotokiari.githubcontributioncalendar.core.Failed
import pl.deniotokiari.githubcontributioncalendar.core.Success
import pl.deniotokiari.githubcontributioncalendar.data.model.DataError
import pl.deniotokiari.githubcontributioncalendar.data.repository.AppConfigurationRepository
import pl.deniotokiari.githubcontributioncalendar.domain.model.DomainError
import pl.deniotokiari.githubcontributioncalendar.domain.model.Email

class GetSupportEmailUseCaseTest {

    private lateinit var appConfigurationRepository: AppConfigurationRepository
    private lateinit var useCase: GetSupportEmailUseCase

    @Before
    fun setUp() {
        appConfigurationRepository = mockk()
        useCase = GetSupportEmailUseCase(appConfigurationRepository)
    }

    @Test
    fun `should return success with email when repository returns success`() = runTest {
        // Given
        val emailString = "support@example.com"
        coEvery { appConfigurationRepository.getSupportEmail() } returns Success(emailString)

        // When
        val result = useCase(Unit)

        // Then
        assertTrue(result is Success)
        val email = (result as Success).value
        assertTrue(email is Email)
        assertEquals(emailString, email.value)
    }

    @Test
    fun `should return failed with domain error when repository returns failed`() = runTest {
        // Given
        val throwable = RuntimeException("Network error")
        val dataError = DataError(throwable)
        coEvery { appConfigurationRepository.getSupportEmail() } returns Failed(dataError)

        // When
        val result = useCase(Unit)

        // Then
        assertTrue(result is Failed)
        val domainError = (result as Failed).value
        assertTrue(domainError is DomainError)
        assertEquals(throwable, domainError.throwable)
    }

    @Test
    fun `should handle empty email string`() = runTest {
        // Given
        val emptyEmail = ""
        coEvery { appConfigurationRepository.getSupportEmail() } returns Success(emptyEmail)

        // When
        val result = useCase(Unit)

        // Then
        assertTrue(result is Success)
        val email = (result as Success).value
        assertTrue(email is Email)
        assertEquals(emptyEmail, email.value)
    }

    @Test
    fun `should handle long email string`() = runTest {
        // Given
        val longEmail = "very.long.email.address@very.long.domain.name.com"
        coEvery { appConfigurationRepository.getSupportEmail() } returns Success(longEmail)

        // When
        val result = useCase(Unit)

        // Then
        assertTrue(result is Success)
        val email = (result as Success).value
        assertTrue(email is Email)
        assertEquals(longEmail, email.value)
    }
}
