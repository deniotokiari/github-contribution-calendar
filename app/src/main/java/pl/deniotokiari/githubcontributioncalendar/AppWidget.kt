package pl.deniotokiari.githubcontributioncalendar

import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
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
import pl.deniotokiari.githubcontributioncalendar.ui.theme.Purple80
import kotlin.math.roundToInt

class AppWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val defaultColor = Purple80.toArgb()
            val size = LocalSize.current
            val params = WidgetBitmapCreator.getParamsForBitmap(
                width = size.width.value.roundToInt(),
                height = size.height.value.roundToInt(),
                squareSize = 20,
                padding = 1
            )
            val colors by ContributionCalendarRepository(apolloClient).getBlocks(
                user = "deniotokiari",
                size = params.hCount * params.wCount,
                defaultColor = defaultColor
            )
                .collectAsState(initial = IntArray(0))
            val bitmap = WidgetBitmapCreator()(
                width = size.width.value.roundToInt(),
                height = size.height.value.roundToInt(),
                params = params,
                colors = colors,
                defaultColor = defaultColor
            )

            Spacer(
                modifier = GlanceModifier.fillMaxSize().background(ImageProvider(bitmap))
            )
        }
    }
}