package pl.deniotokiari.githubcontributioncalendar

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pl.deniotokiari.githubcontributioncalendar.ui.theme.GitHubContributionCalendarTheme

class AppWidgetConfigurationActivity : ComponentActivity() {
    private val appWidgetId by lazy {
        intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setResult(Activity.RESULT_CANCELED, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId))

        setContent {
            GitHubContributionCalendarTheme {
                Surface(modifier = Modifier.wrapContentSize(), color = MaterialTheme.colorScheme.background) {
                    var username by remember { mutableStateOf("") }

                    Column {
                        OutlinedTextField(
                            value = username,
                            onValueChange = {
                                username = it
                            },
                            label = { Text(text = "GitHub username") }
                        )
                        Row {
                            TextButton(onClick = {
                                finish()
                            }) {
                                Text(text = stringResource(id = android.R.string.cancel))
                            }
                            TextButton(
                                onClick = {
                                    val glanceAppWidgetManager =
                                        GlanceAppWidgetManager(this@AppWidgetConfigurationActivity)
                                    val glanceId: GlanceId = glanceAppWidgetManager.getGlanceIdBy(appWidgetId)

                                    lifecycleScope.launch {
                                        updateAppWidgetState(
                                            context = this@AppWidgetConfigurationActivity,
                                            glanceId = glanceId
                                        ) {
                                            it[stringPreferencesKey("username")] = username
                                        }

                                        UpdateAppWidgetWorker.start(this@AppWidgetConfigurationActivity)

                                        setResult(
                                            Activity.RESULT_OK,
                                            Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                                        )
                                        finish()
                                    }
                                },
                                enabled = username.isNotEmpty()
                            ) {
                                Text(text = stringResource(id = android.R.string.ok))
                            }
                        }
                    }
                }
            }
        }
    }
}