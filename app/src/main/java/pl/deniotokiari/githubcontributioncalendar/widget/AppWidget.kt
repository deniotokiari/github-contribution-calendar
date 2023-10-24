package pl.deniotokiari.githubcontributioncalendar.widget

import android.content.Context
import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceComposable
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
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextDefaults
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import pl.deniotokiari.githubcontributioncalendar.BuildConfig
import pl.deniotokiari.githubcontributioncalendar.DevRepository
import pl.deniotokiari.githubcontributioncalendar.R
import pl.deniotokiari.githubcontributioncalendar.activity.MainActivity
import pl.deniotokiari.githubcontributioncalendar.analytics.AppAnalytics
import pl.deniotokiari.githubcontributioncalendar.core.px
import pl.deniotokiari.githubcontributioncalendar.data.decode
import pl.deniotokiari.githubcontributioncalendar.etc.BlocksBitmapCreator
import pl.deniotokiari.githubcontributioncalendar.widget.usecase.RemoveWidgetByUserNameAndWidgetIdUseCase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import kotlin.math.roundToInt

class AppWidget : GlanceAppWidget(), KoinComponent {
    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun onDelete(context: Context, glanceId: GlanceId) {
        val prefs = getAppWidgetState<Preferences>(context, glanceId)
        val username = prefs[USER_NAME_KEY]
        val widgetId = prefs[WIDGET_ID_KEY]

        if (username != null && widgetId != null) {
            val removeWidgetByUserNameAndWidgetIdUseCase: RemoveWidgetByUserNameAndWidgetIdUseCase by inject()

            removeWidgetByUserNameAndWidgetIdUseCase(
                RemoveWidgetByUserNameAndWidgetIdUseCase.Params(
                    widgetId = widgetId,
                    userName = username
                )
            )

            get<AppAnalytics>().trackWidgetRemove(username)
        }

        super.onDelete(context, glanceId)
    }

    @GlanceComposable
    @Composable
    private fun Loading() {
        Box(
            modifier = GlanceModifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    @GlanceComposable
    @Composable
    private fun Empty(userName: String) {
        Box(
            modifier = GlanceModifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.no_data_for, userName),
                style = TextDefaults.defaultTextStyle.copy(textAlign = TextAlign.Center)
            )
        }
    }

    @GlanceComposable
    @Composable
    private fun Content(
        userName: String,
        widgetId: Int,
        config: WidgetConfiguration,
        colors: List<Int>
    ) {
        val bitmapCreator: BlocksBitmapCreator by inject()
        val blockSize = config.blockSize
        val padding = config.padding
        val size = LocalSize.current
        val width = size.width.value.roundToInt().px
        val height = size.height.value.roundToInt().px
        val params = bitmapCreator.getParamsForBitmap(
            width = width,
            height = height,
            squareSize = blockSize,
            padding = padding,
            colorsSize = colors.size,
            opacity = config.opacity
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
                            DESTINATION_USER_KEY to userName,
                            DESTINATION_WIDGET_ID_KEY to widgetId
                        )
                    )
                )
        )

        if (BuildConfig.DEBUG) {
            val time = LocalDateTime.now().format(
                DateTimeFormatterBuilder()
                    .appendValue(ChronoField.HOUR_OF_DAY, 2)
                    .appendLiteral(':')
                    .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
                    .optionalStart()
                    .appendLiteral(':')
                    .appendValue(ChronoField.SECOND_OF_MINUTE, 2).toFormatter()
            )
            val count by get<DevRepository>().widgetUpdateCount().collectAsState(initial = 0)

            Text(
                modifier = GlanceModifier.padding(6.dp).background(Color.White),
                text = "${count}: $time"
            )
        }
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        Log.d("LOG", "provideGlance for $id")

        provideContent {
            val username = currentState(key = USER_NAME_KEY)
            val widgetId = currentState(key = WIDGET_ID_KEY)
            val config = currentState(key = CONFIG_KEY)?.let(WidgetConfiguration::decode)
            val colors = currentState(key = COLORS_KEY)?.let { it.decode() }

            if (username != null && widgetId != null && config != null && colors != null) {
                Log.d("LOG", "provideContent for $username $id colors => ${colors.size}")

                if (colors.isEmpty()) {
                    Empty(userName = username)
                } else {
                    Content(userName = username, widgetId = widgetId, config = config, colors = colors)
                }
            } else {
                Loading()
            }
        }
    }

    companion object {
        val USER_NAME_KEY = stringPreferencesKey("username")
        val WIDGET_ID_KEY = intPreferencesKey("widgetId")
        val CONFIG_KEY = stringPreferencesKey("config")
        val COLORS_KEY = stringPreferencesKey("colors")

        const val DESTINATION_USER = "DESTINATION_USER"
        const val DESTINATION_WIDGET_ID = "DESTINATION_WIDGET_ID"
        val DESTINATION_USER_KEY = ActionParameters.Key<String>(DESTINATION_USER)
        val DESTINATION_WIDGET_ID_KEY = ActionParameters.Key<Int>(DESTINATION_WIDGET_ID)
    }
}