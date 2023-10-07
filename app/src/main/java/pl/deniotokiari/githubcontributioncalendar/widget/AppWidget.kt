package pl.deniotokiari.githubcontributioncalendar.widget

import android.content.Context
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import pl.deniotokiari.githubcontributioncalendar.core.px
import pl.deniotokiari.githubcontributioncalendar.data.ContributionCalendarRepository
import pl.deniotokiari.githubcontributioncalendar.etc.BlocksBitmapCreator
import kotlin.math.roundToInt

class AppWidget : GlanceAppWidget(), KoinComponent {
    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun onDelete(context: Context, glanceId: GlanceId) {
        val prefs = getAppWidgetState<Preferences>(context, glanceId)
        val username = prefs[USER_NAME_KEY]

        if (username != null) {
            val repository: ContributionCalendarRepository by inject()
            repository.removeContributionsForUser(username)
        }

        super.onDelete(context, glanceId)
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository: ContributionCalendarRepository by inject()
        val bitmapCreator: BlocksBitmapCreator by inject()

        provideContent {
            val username = currentState(key = USER_NAME_KEY)

            if (username != null) {
                Log.d("LOG", "provideContent for $username")
                val items = remember { repository.contributionsByUser(username) }
                val colors by items.collectAsState(initial = emptyList())
                val blockSize = currentState(key = BLOCK_SIZE_KEY)
                val padding = currentState(key = PADDING_KEY)

                if (colors.isEmpty()) {
                    Box(
                        modifier = GlanceModifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    val size = LocalSize.current
                    val width = size.width.value.roundToInt().px
                    val height = size.height.value.roundToInt().px
                    val params = bitmapCreator.getParamsForBitmap(
                        width = width,
                        height = height,
                        squareSize = blockSize ?: BlocksBitmapCreator.DEFAULT_BLOCK_SIZE,
                        padding = padding ?: BlocksBitmapCreator.DEFAULT_PADDING,
                        colorsSize = colors.size
                    )
                    val blocksCount = params.blocksCount
                    val offset = colors.size - blocksCount
                    val bitmap = bitmapCreator(
                        width = width,
                        height = height,
                        params = params,
                        colors = IntArray(blocksCount) { colors[it + offset] }
                    )
                    Image(
                        provider = ImageProvider(bitmap),
                        contentDescription = "blocks",
                        modifier = GlanceModifier.fillMaxSize()
                    )
                }
            }
        }
    }

    companion object {
        val USER_NAME_KEY = stringPreferencesKey("username")
        val BLOCK_SIZE_KEY = intPreferencesKey("blockSize")
        val PADDING_KEY = intPreferencesKey("padding")
        val OPACITY_SIZE_KEY = intPreferencesKey("opacity")
    }
}
