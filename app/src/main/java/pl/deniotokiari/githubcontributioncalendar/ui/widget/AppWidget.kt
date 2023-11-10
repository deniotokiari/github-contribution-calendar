package pl.deniotokiari.githubcontributioncalendar.ui.widget

import android.content.Context
import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
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
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextDefaults
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import pl.deniotokiari.githubcontributioncalendar.R
import pl.deniotokiari.githubcontributioncalendar.analytics.AppAnalytics
import pl.deniotokiari.githubcontributioncalendar.core.px
import pl.deniotokiari.githubcontributioncalendar.core.successOrNull
import pl.deniotokiari.githubcontributioncalendar.data.model.Contributions
import pl.deniotokiari.githubcontributioncalendar.data.model.UserName
import pl.deniotokiari.githubcontributioncalendar.data.model.WidgetConfiguration
import pl.deniotokiari.githubcontributioncalendar.data.model.WidgetId
import pl.deniotokiari.githubcontributioncalendar.data.repository.BitmapRepository
import pl.deniotokiari.githubcontributioncalendar.domain.model.WidgetIdentifiers
import pl.deniotokiari.githubcontributioncalendar.domain.usecase.RemoveWidgetDataUseCase
import pl.deniotokiari.githubcontributioncalendar.ui.activity.MainActivity
import kotlin.math.roundToInt

class AppWidget : GlanceAppWidget(), KoinComponent {
    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun onDelete(context: Context, glanceId: GlanceId) {
        val prefs = getAppWidgetState<Preferences>(context, glanceId)
        val username = prefs[USER_NAME_KEY]
        val widgetId = prefs[WIDGET_ID_KEY]

        if (username != null && widgetId != null) {
            val removeWidgetDataUseCase: RemoveWidgetDataUseCase by inject()

            removeWidgetDataUseCase(
                WidgetIdentifiers(
                    userName = UserName(username),
                    widgetId = WidgetId(widgetId)
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
        colors: List<Int>,
        bitmapRepository: BitmapRepository
    ) {
        val blockSize = config.blockSize
        val padding = config.padding
        val size = LocalSize.current
        val width = size.width.value.roundToInt().px
        val height = size.height.value.roundToInt().px
        val params = bitmapRepository.getMetaData(
            width = width,
            height = height,
            blockSize = blockSize.value,
            colorsSize = colors.size,
        ).successOrNull() ?: return
        val blocksCount = params.hCount * params.wCount
        val offset = colors.size - blocksCount
        val bitmap = bitmapRepository.getBitmap(
            width = width,
            height = height,
            colors = IntArray(blocksCount) { colors[it + offset] }.toList(),
            blockSize = blockSize.value,
            padding = padding.value,
            opacity = config.opacity.value
        ).successOrNull() ?: return
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
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        Log.d("LOG", "provideGlance for $id")

        provideContent {
            val username = currentState(key = USER_NAME_KEY)
            val widgetId = currentState(key = WIDGET_ID_KEY)
            val config = currentState(key = CONFIG_KEY)?.let(WidgetConfiguration::fromLocalModel)
            val contributions = currentState(key = COLORS_KEY)?.let(Contributions::fromLocalModel)

            if (username != null && widgetId != null && config != null && contributions != null) {
                Log.d("LOG", "provideContent for $username $id colors => ${contributions.colors.size}")

                if (contributions.colors.isEmpty()) {
                    Empty(userName = username)
                } else {
                    Content(
                        userName = username,
                        widgetId = widgetId,
                        config = config,
                        colors = contributions.asIntColors(),
                        bitmapRepository = get()
                    )
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