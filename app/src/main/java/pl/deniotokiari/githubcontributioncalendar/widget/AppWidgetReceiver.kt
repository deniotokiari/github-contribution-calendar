package pl.deniotokiari.githubcontributioncalendar.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.work.WorkManager
import org.koin.core.component.KoinComponent

class AppWidgetReceiver : GlanceAppWidgetReceiver(), KoinComponent {
    override val glanceAppWidget: GlanceAppWidget = AppWidget()

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)

        context?.let { UpdateAppWidgetWorker.cancel(it) }
    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)

        context?.let { UpdateAppWidgetWorker.start(it) }
    }
}