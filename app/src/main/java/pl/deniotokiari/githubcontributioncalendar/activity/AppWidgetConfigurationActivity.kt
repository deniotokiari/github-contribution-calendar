package pl.deniotokiari.githubcontributioncalendar.activity

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pl.deniotokiari.githubcontributioncalendar.widget.AppWidget
import pl.deniotokiari.githubcontributioncalendar.ui.theme.GitHubContributionCalendarTheme
import pl.deniotokiari.githubcontributioncalendar.widget.UpdateAppWidgetWorker

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

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
        }

        setContent {
            GitHubContributionCalendarTheme {
                Surface(modifier = Modifier.wrapContentSize(), color = MaterialTheme.colorScheme.background) {
                    var username by remember { mutableStateOf("") }

                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        OutlinedTextField(
                            value = username,
                            onValueChange = {
                                username = it
                            },
                            label = { Text(text = "GitHub username") }
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        )
                        {
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
                                            it[AppWidget.USER_NAME_KEY] = username
                                        }

                                        UpdateAppWidgetWorker.start(this@AppWidgetConfigurationActivity)

                                        AppWidget().update(this@AppWidgetConfigurationActivity, glanceId)

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