package pl.deniotokiari.githubcontributioncalendar

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import kotlin.math.roundToInt

class AppWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val size = LocalSize.current
            val bitmap = Repo().getBitmap(
                width = size.width.value.roundToInt(),
                height = size.height.value.roundToInt()
            )

            Spacer(
                modifier = GlanceModifier.fillMaxSize().background(ImageProvider(bitmap))
            )
        }
    }
}