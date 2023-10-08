package pl.deniotokiari.githubcontributioncalendar.widget

import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import pl.deniotokiari.githubcontributioncalendar.activity.MainActivity
import pl.deniotokiari.githubcontributioncalendar.core.px
import pl.deniotokiari.githubcontributioncalendar.data.ContributionCalendarRepository
import pl.deniotokiari.githubcontributioncalendar.etc.BlocksBitmapCreator
import pl.deniotokiari.githubcontributioncalendar.widget.usecase.RemoveWidgetByUserNameAndWidgetIdUseCase
import kotlin.math.roundToInt

class AppWidget : GlanceAppWidget(), KoinComponent {
    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun onDelete(context: Context, glanceId: GlanceId) {
        val prefs = getAppWidgetState<Preferences>(context, glanceId)
        val username = prefs[USER_NAME_KEY]

        if (username != null) {
            val removeWidgetByUserNameAndWidgetIdUseCase: RemoveWidgetByUserNameAndWidgetIdUseCase by inject()
            val widgetId = glanceId.getWidgetId(context)

            removeWidgetByUserNameAndWidgetIdUseCase(
                RemoveWidgetByUserNameAndWidgetIdUseCase.Params(
                    widgetId = widgetId,
                    userName = username
                )
            )
        }

        super.onDelete(context, glanceId)
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val contributionCalendarRepository: ContributionCalendarRepository by inject()
        val widgetConfigurationRepository: WidgetConfigurationRepository by inject()
        val bitmapCreator: BlocksBitmapCreator by inject()

        provideContent {
            val username = currentState(key = USER_NAME_KEY)

            if (username != null) {
                val items = remember { contributionCalendarRepository.contributionsByUser(username) }
                val widgetConfig = remember {
                    widgetConfigurationRepository.configurationByWidgetIdAndUserName(
                        widgetId = id.getWidgetId(context),
                        userName = username
                    )
                }
                val colors by items.collectAsState(initial = emptyList())
                val config by widgetConfig.collectAsState(initial = WidgetConfiguration.default())
                val blockSize = config.blockSize
                val padding = config.padding

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
                        modifier = GlanceModifier
                            .fillMaxSize()
                            .clickable(
                                actionStartActivity<MainActivity>(
                                    parameters = actionParametersOf(
                                        DESTINATION_USER_KEY to username,
                                        DESTINATION_WIDGET_ID_KEY to id.getWidgetId(context)
                                    )
                                )
                            )
                    )
                }
            }
        }
    }

    companion object {
        val USER_NAME_KEY = stringPreferencesKey("username")
        const val DESTINATION_USER = "DESTINATION_USER"
        const val DESTINATION_WIDGET_ID = "DESTINATION_WIDGET_ID"
        val DESTINATION_USER_KEY = ActionParameters.Key<String>(DESTINATION_USER)
        val DESTINATION_WIDGET_ID_KEY = ActionParameters.Key<Int>(DESTINATION_WIDGET_ID)
    }
}

private fun GlanceId.getWidgetId(context: Context): Int = GlanceAppWidgetManager(context).getAppWidgetId(this)