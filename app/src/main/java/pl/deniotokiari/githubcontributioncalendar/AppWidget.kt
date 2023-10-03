package pl.deniotokiari.githubcontributioncalendar

import android.content.Context
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme.colors
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.state.GlanceState
import pl.deniotokiari.githubcontributioncalendar.ui.theme.Purple80
import kotlin.math.roundToInt

class AppWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Log.d("LOG", "provide content")
            val username = currentState(key = stringPreferencesKey("username"))

            if (username != null) {
                val colors by ContributionCalendarRepository(apolloClient).getBlocks(user = username)
                    .collectAsState(initial = emptyList())

                Log.d("LOG", "provide content colors => ${colors.size}")

                val defaultColor = Purple80.toArgb()
                val size = LocalSize.current
                val params = WidgetBitmapCreator.getParamsForBitmap(
                    width = size.width.value.roundToInt(),
                    height = size.height.value.roundToInt(),
                    squareSize = 20,
                    padding = 1,
                    colorsSize = colors.size
                )
                val blocksCount = params.blocksCount
                val offset = colors.size - blocksCount
                val bitmap = WidgetBitmapCreator()(
                    width = size.width.value.roundToInt(),
                    height = size.height.value.roundToInt(),
                    params = params,
                    colors = IntArray(blocksCount) {
                        colors.getOrNull(it + offset) ?: defaultColor
                    },
                    defaultColor = defaultColor
                )
                Spacer(
                    modifier = GlanceModifier.fillMaxSize().background(ImageProvider(bitmap))
                )
            }
        }

    }
}