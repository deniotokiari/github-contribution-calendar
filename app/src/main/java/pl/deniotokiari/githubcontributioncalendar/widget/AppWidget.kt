package pl.deniotokiari.githubcontributioncalendar.widget

import android.content.Context
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import org.koin.compose.rememberKoinInject
import pl.deniotokiari.githubcontributioncalendar.ui.theme.Purple80
import kotlin.math.roundToInt

class AppWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Log.d("LOG", "provideContent start")
            val username = currentState(key = USER_NAME_KEY)
            val repository: ContributionCalendarRepository = rememberKoinInject()
            val bitmapCreator: WidgetBitmapCreator = rememberKoinInject()
            val defaultColor = remember { Purple80.toArgb() }

            if (username != null) {
                val colors by repository.getBlocks(user = username).collectAsState(initial = emptyList())
                val size = LocalSize.current
                val params = bitmapCreator.getParamsForBitmap(
                    width = size.width.value.roundToInt(),
                    height = size.height.value.roundToInt(),
                    squareSize = 20,
                    padding = 1,
                    colorsSize = colors.size
                )
                val blocksCount = params.blocksCount
                val offset = colors.size - blocksCount
                val bitmap = bitmapCreator(
                    width = size.width.value.roundToInt(),
                    height = size.height.value.roundToInt(),
                    params = params,
                    colors = IntArray(blocksCount) { colors.getOrNull(it + offset) ?: defaultColor },
                    defaultColor = defaultColor
                )
                Spacer(
                    modifier = GlanceModifier.fillMaxSize().background(ImageProvider(bitmap))
                )
            }
        }

    }

    companion object {
        val USER_NAME_KEY = stringPreferencesKey("username")
    }
}