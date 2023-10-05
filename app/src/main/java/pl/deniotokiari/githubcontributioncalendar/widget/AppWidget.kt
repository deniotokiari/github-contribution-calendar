package pl.deniotokiari.githubcontributioncalendar.widget

import android.content.Context
import android.widget.RemoteViews
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.wrapContentSize
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import pl.deniotokiari.githubcontributioncalendar.R
import pl.deniotokiari.githubcontributioncalendar.etc.BlocksBitmapCreator
import pl.deniotokiari.githubcontributioncalendar.ui.theme.Purple40
import pl.deniotokiari.githubcontributioncalendar.widget.data.ContributionCalendarRepository
import kotlin.math.roundToInt

class AppWidget : GlanceAppWidget(), KoinComponent {
    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun onDelete(context: Context, glanceId: GlanceId) {
        val prefs = getAppWidgetState<Preferences>(context, glanceId)
        val username = prefs[USER_NAME_KEY]

        if (username != null) {
            val repository: ContributionCalendarRepository by inject()
            repository.removeBlocksForUser(username)
        }

        super.onDelete(context, glanceId)
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository: ContributionCalendarRepository by inject()
        val bitmapCreator: BlocksBitmapCreator by inject()

        provideContent {
            val username = currentState(key = USER_NAME_KEY)

            if (username != null) {
                val items = remember { repository.getBlocks(username) }
                val colors by items.collectAsState(initial = emptyList())

                if (colors.isEmpty()) {
                    Box(
                        modifier = GlanceModifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        AndroidRemoteViews(
                            remoteViews = RemoteViews(context.packageName, R.layout.prgoress),
                            modifier = GlanceModifier.wrapContentSize()
                        )
                    }
                } else {
                    val size = LocalSize.current
                    val width = size.width.value.roundToInt()
                    val height = size.height.value.roundToInt()
                    val params = bitmapCreator.getParamsForBitmap(
                        width = width,
                        height = height,
                        squareSize = 20,
                        padding = 1,
                        colorsSize = colors.size
                    )
                    val blocksCount = params.blocksCount
                    val offset = colors.size - blocksCount
                    val bitmap = bitmapCreator(
                        width = width,
                        height = height,
                        params = params,
                        colors = IntArray(blocksCount) { colors.getOrNull(it + offset) ?: Purple40.toArgb() }
                    )
                    Spacer(
                        modifier = GlanceModifier.fillMaxSize().background(ImageProvider(bitmap))
                    )
                }
            }
        }
    }

    companion object {
        val USER_NAME_KEY = stringPreferencesKey("username")
    }
}