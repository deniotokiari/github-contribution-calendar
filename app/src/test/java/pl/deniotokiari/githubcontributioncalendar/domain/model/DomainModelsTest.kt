package pl.deniotokiari.githubcontributioncalendar.domain.model

import org.junit.Assert.*
import org.junit.Test
import pl.deniotokiari.githubcontributioncalendar.data.model.UserName
import pl.deniotokiari.githubcontributioncalendar.data.model.WidgetId

class DomainModelsTest {

    @Test
    fun `DomainError should wrap throwable`() {
        // Given
        val throwable = RuntimeException("Test error")

        // When
        val domainError = DomainError(throwable)

        // Then
        assertEquals(throwable, domainError.throwable)
    }

    @Test
    fun `Count should wrap int value`() {
        // Given
        val count = 42

        // When
        val countValue = Count(count)

        // Then
        assertEquals(count, countValue.value)
    }

    @Test
    fun `Count should handle zero`() {
        // Given
        val count = 0

        // When
        val countValue = Count(count)

        // Then
        assertEquals(0, countValue.value)
    }

    @Test
    fun `Count should handle negative values`() {
        // Given
        val count = -5

        // When
        val countValue = Count(count)

        // Then
        assertEquals(-5, countValue.value)
    }

    @Test
    fun `Count should handle large values`() {
        // Given
        val count = Int.MAX_VALUE

        // When
        val countValue = Count(count)

        // Then
        assertEquals(Int.MAX_VALUE, countValue.value)
    }

    @Test
    fun `Email should wrap string value`() {
        // Given
        val email = "test@example.com"

        // When
        val emailValue = Email(email)

        // Then
        assertEquals(email, emailValue.value)
    }

    @Test
    fun `WidgetIdentifiers should contain userName and widgetId`() {
        // Given
        val userName = UserName("testuser")
        val widgetId = WidgetId(123)

        // When
        val widgetIdentifiers = WidgetIdentifiers(userName, widgetId)

        // Then
        assertEquals(userName, widgetIdentifiers.userName)
        assertEquals(widgetId, widgetIdentifiers.widgetId)
    }

    @Test
    fun `WidgetConfigurationWithContributions should contain all properties`() {
        // Given
        val userName = UserName("testuser")
        val widgetId = WidgetId(123)
        val config = pl.deniotokiari.githubcontributioncalendar.data.model.WidgetConfiguration(
            padding = pl.deniotokiari.githubcontributioncalendar.data.model.Padding(2),
            opacity = pl.deniotokiari.githubcontributioncalendar.data.model.Opacity(100),
            blockSize = pl.deniotokiari.githubcontributioncalendar.data.model.BlockSize(20)
        )
        val contributions = pl.deniotokiari.githubcontributioncalendar.data.model.Contributions(listOf("#000000"))

        // When
        val widgetConfigWithContributions = WidgetConfigurationWithContributions(
            configuration = config,
            contributions = contributions,
            userName = userName,
            widgetId = widgetId
        )

        // Then
        assertEquals(config, widgetConfigWithContributions.configuration)
        assertEquals(contributions, widgetConfigWithContributions.contributions)
        assertEquals(userName, widgetConfigWithContributions.userName)
        assertEquals(widgetId, widgetConfigWithContributions.widgetId)
    }

    @Test
    fun `DomainError equals should work correctly`() {
        // Given
        val throwable1 = RuntimeException("Error 1")
        val throwable2 = RuntimeException("Error 2")

        // When
        val domainError1 = DomainError(throwable1)
        val domainError2 = DomainError(throwable2)

        // Then
        assertNotEquals(domainError1, domainError2)
    }

    @Test
    fun `Count equals should work correctly`() {
        // Given
        val count1 = Count(5)
        val count2 = Count(5)
        val count3 = Count(10)

        // When & Then
        assertEquals(count1, count2)
        assertNotEquals(count1, count3)
    }

    @Test
    fun `Email equals should work correctly`() {
        // Given
        val email1 = Email("test@example.com")
        val email2 = Email("test@example.com")
        val email3 = Email("other@example.com")

        // When & Then
        assertEquals(email1, email2)
        assertNotEquals(email1, email3)
    }

    @Test
    fun `WidgetIdentifiers equals should work correctly`() {
        // Given
        val userName1 = UserName("user1")
        val userName2 = UserName("user2")
        val widgetId1 = WidgetId(123)
        val widgetId2 = WidgetId(456)
        
        val widgetId1a = WidgetIdentifiers(userName1, widgetId1)
        val widgetId1b = WidgetIdentifiers(userName1, widgetId1)
        val widgetId2a = WidgetIdentifiers(userName2, widgetId2)

        // When & Then
        assertEquals(widgetId1a, widgetId1b)
        assertNotEquals(widgetId1a, widgetId2a)
    }

    @Test
    fun `DomainError hashCode should work correctly`() {
        // Given
        val throwable1 = RuntimeException("Error 1")
        val throwable2 = RuntimeException("Error 2")

        // When
        val domainError1 = DomainError(throwable1)
        val domainError2 = DomainError(throwable2)

        // Then
        assertNotEquals(domainError1.hashCode(), domainError2.hashCode())
    }

    @Test
    fun `Count hashCode should work correctly`() {
        // Given
        val count1 = Count(5)
        val count2 = Count(5)
        val count3 = Count(10)

        // When & Then
        assertEquals(count1.hashCode(), count2.hashCode())
        assertNotEquals(count1.hashCode(), count3.hashCode())
    }

    @Test
    fun `Email hashCode should work correctly`() {
        // Given
        val email1 = Email("test@example.com")
        val email2 = Email("test@example.com")
        val email3 = Email("other@example.com")

        // When & Then
        assertEquals(email1.hashCode(), email2.hashCode())
        assertNotEquals(email1.hashCode(), email3.hashCode())
    }

    @Test
    fun `WidgetIdentifiers hashCode should work correctly`() {
        // Given
        val userName1 = UserName("user1")
        val userName2 = UserName("user2")
        val widgetId1 = WidgetId(123)
        val widgetId2 = WidgetId(456)
        
        val widgetId1a = WidgetIdentifiers(userName1, widgetId1)
        val widgetId1b = WidgetIdentifiers(userName1, widgetId1)
        val widgetId2a = WidgetIdentifiers(userName2, widgetId2)

        // When & Then
        assertEquals(widgetId1a.hashCode(), widgetId1b.hashCode())
        assertNotEquals(widgetId1a.hashCode(), widgetId2a.hashCode())
    }
}