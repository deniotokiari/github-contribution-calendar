package pl.deniotokiari.githubcontributioncalendar.analytics

import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import pl.deniotokiari.githubcontributioncalendar.data.model.BlockSize
import pl.deniotokiari.githubcontributioncalendar.data.model.Opacity
import pl.deniotokiari.githubcontributioncalendar.data.model.Padding
import pl.deniotokiari.githubcontributioncalendar.data.model.WidgetConfiguration

class AppAnalyticsTest {

    private lateinit var analytics: Analytics
    private lateinit var appAnalytics: AppAnalytics

    @Before
    fun setUp() {
        analytics = mockk(relaxed = true)
        appAnalytics = AppAnalytics(analytics)
    }

    @Test
    fun `trackWidgetAdd should call analytics with correct parameters`() {
        // Given
        val userName = "testuser"

        // When
        appAnalytics.trackWidgetAdd(userName)

        // Then
        verify { analytics.track("widget_add", mapOf("user" to userName)) }
    }

    @Test
    fun `trackWidgetRemove should call analytics with correct parameters`() {
        // Given
        val userName = "testuser"

        // When
        appAnalytics.trackWidgetRemove(userName)

        // Then
        verify { analytics.track("widget_remove", mapOf("user" to userName)) }
    }

    @Test
    fun `trackWidgetConfigUpdate should call analytics with correct parameters`() {
        // Given
        val userName = "testuser"
        val config = WidgetConfiguration(
            padding = Padding(5),
            opacity = Opacity(80),
            blockSize = BlockSize(30)
        )

        // When
        appAnalytics.trackWidgetConfigUpdate(userName, config)

        // Then
        verify { 
            analytics.track(
                "widget_update",
                mapOf(
                    "user" to userName,
                    "block_size" to config.blockSize,
                    "padding" to config.padding,
                    "opacity" to config.opacity
                )
            ) 
        }
    }

    @Test
    fun `trackHomeRefresh should call analytics with correct parameters`() {
        // Given
        val size = 5

        // When
        appAnalytics.trackHomeRefresh(size)

        // Then
        verify { analytics.track("refresh_home", mapOf("size" to size)) }
    }

    @Test
    fun `trackUserRefresh should call analytics with correct parameters`() {
        // Given
        val user = "testuser"

        // When
        appAnalytics.trackUserRefresh(user)

        // Then
        verify { analytics.track("refresh_user", mapOf("user" to user)) }
    }

    @Test
    fun `trackHomeView should call analytics with correct parameters`() {
        // When
        appAnalytics.trackHomeView()

        // Then
        verify { analytics.track("view_home", mapOf()) }
    }

    @Test
    fun `trackUserView should call analytics with correct parameters`() {
        // Given
        val user = "testuser"

        // When
        appAnalytics.trackUserView(user)

        // Then
        verify { analytics.track("view_user", mapOf("user" to user)) }
    }

    @Test
    fun `trackAboutView should call analytics with correct parameters`() {
        // When
        appAnalytics.trackAboutView()

        // Then
        verify { analytics.track("view_about", mapOf()) }
    }

    @Test
    fun `trackOpenSupportEmail should call analytics with correct parameters`() {
        // When
        appAnalytics.trackOpenSupportEmail()

        // Then
        verify { analytics.track("support_email", mapOf()) }
    }

    @Test
    fun `trackOpenSupportEmailFailed should call analytics with correct parameters`() {
        // When
        appAnalytics.trackOpenSupportEmailFailed()

        // Then
        verify { analytics.track("support_email_failed", mapOf()) }
    }

    @Test
    fun `trackIsIgnoringBatteryOptimizations should call analytics with correct parameters`() {
        // Given
        val value = true

        // When
        appAnalytics.trackIsIgnoringBatteryOptimizations(value)

        // Then
        verify { analytics.track("is_ignoring_battery_optimizations", mapOf("ignoring" to value)) }
    }

    @Test
    fun `trackAllWidgetsUpdate should call analytics with correct parameters`() {
        // Given
        val count = 5
        val time = 1000L

        // When
        appAnalytics.trackAllWidgetsUpdate(count, time)

        // Then
        verify { analytics.track("all_widgets_update", mapOf("count" to count, "time" to time)) }
    }
}
