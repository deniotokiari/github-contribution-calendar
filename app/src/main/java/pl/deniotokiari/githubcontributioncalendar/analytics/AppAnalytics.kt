package pl.deniotokiari.githubcontributioncalendar.analytics

import pl.deniotokiari.githubcontributioncalendar.widget.WidgetConfiguration

class AppAnalytics(
    private val analytics: Analytics
) {
    fun trackWidgetAdd(userName: String) {
        analytics.track("widget_add", mapOf("user" to userName))
    }

    fun trackWidgetRemove(userName: String) {
        analytics.track("widget_remove", mapOf("user" to userName))
    }

    fun trackWidgetConfigUpdate(userName: String, config: WidgetConfiguration) {
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

    fun trackHomeRefresh(size: Int) {
        analytics.track("refresh_home", mapOf("size" to size))
    }

    fun trackUserRefresh() {
        analytics.track("refresh_user", mapOf())
    }

    fun trackHomeView() {
        analytics.track("view_home", mapOf())
    }

    fun trackUserView(user: String) {
        analytics.track("view_user", mapOf("user" to user))
    }

    fun trackIsIgnoringBatteryOptimizations(value: Boolean) {
        analytics.track("is_ignoring_battery_optimizations", mapOf("ignoring" to value))
    }
}