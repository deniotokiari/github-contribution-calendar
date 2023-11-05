package pl.deniotokiari.githubcontributioncalendar.ui.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import org.koin.core.component.KoinComponent
import pl.deniotokiari.githubcontributioncalendar.ui.activity.AppWidgetConfigurationActivity

class AppWidgetReceiver : GlanceAppWidgetReceiver(), KoinComponent {
    override val glanceAppWidget: GlanceAppWidget = AppWidget()

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Log.d("LOG", "AppWidgetReceiver onUpdate => $appWidgetIds")

        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("LOG", "AppWidgetReceiver onReceive => ${intent.action}")

        super.onReceive(context, intent)

        when (intent.action) {
            ACTION_CONFIGURE_LATEST -> {
                val widgetId = AppWidgetManager.getInstance(context)
                    .getAppWidgetIds(ComponentName(context, AppWidgetReceiver::class.java)).lastOrNull()

                if (widgetId != null) {
                    val configIntent = Intent(context, AppWidgetConfigurationActivity::class.java)
                    configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                    configIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                    startActivity(context, configIntent, null)
                } else {
                    Log.d("LOG", "AppWidgetReceiver => ACTION_CONFIGURE_LATEST for null widgetId")
                }
            }
        }
    }

    companion object {
        const val ACTION_CONFIGURE_LATEST = "ACTION_CONFIGURE_LATEST"
    }
}