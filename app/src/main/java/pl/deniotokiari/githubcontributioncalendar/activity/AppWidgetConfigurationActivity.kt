package pl.deniotokiari.githubcontributioncalendar.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.deniotokiari.githubcontributioncalendar.ui.theme.GitHubContributionCalendarTheme
import pl.deniotokiari.githubcontributioncalendar.widget.SetUpAppWidgetWorker
import pl.deniotokiari.githubcontributioncalendar.widget.UpdateAppWidgetWorker


class AppWidgetConfigurationActivity : ComponentActivity() {
    private val appWidgetId by lazy {
        intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        setContent { MainContent() }
    }

    @SuppressLint("BatteryLife")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setResult(Activity.RESULT_CANCELED, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId))

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
        }

        val powerManager = getSystemService(POWER_SERVICE) as? PowerManager

        if (powerManager?.isIgnoringBatteryOptimizations(packageName) == true) {
            setContent { MainContent() }
        } else {
            launcher.launch(Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:$packageName")
            })
        }
    }

    @Composable
    private fun MainContent() {
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
                        label = { Text(text = "GitHub username") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    )
                    {
                        var okEnabled by remember { mutableStateOf(true) }

                        TextButton(onClick = {
                            finish()
                        }) {
                            Text(text = stringResource(id = android.R.string.cancel))
                        }
                        TextButton(
                            onClick = {
                                okEnabled = false

                                SetUpAppWidgetWorker.start(
                                    context = this@AppWidgetConfigurationActivity,
                                    widgetId = appWidgetId,
                                    userName = username
                                )

                                UpdateAppWidgetWorker.start(this@AppWidgetConfigurationActivity)

                                setResult(
                                    Activity.RESULT_OK,
                                    Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                                )

                                finish()
                            },
                            enabled = username.isNotEmpty() && okEnabled
                        ) {
                            Text(text = stringResource(id = android.R.string.ok))

                        }
                    }
                }
            }
        }
    }
}