package pl.deniotokiari.githubcontributioncalendar.ui.compose

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pullrefresh.PullRefreshIndicator
import androidx.compose.material3.pullrefresh.pullRefresh
import androidx.compose.material3.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import pl.deniotokiari.githubcontributioncalendar.R
import pl.deniotokiari.githubcontributioncalendar.ui.activity.LocalNavController
import pl.deniotokiari.githubcontributioncalendar.ui.navigation.AboutRoute
import pl.deniotokiari.githubcontributioncalendar.ui.navigation.UserRoute
import pl.deniotokiari.githubcontributioncalendar.ui.viewmodel.HomeViewModel
import pl.deniotokiari.githubcontributioncalendar.ui.widget.AppWidgetReceiver

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel()
) {
    val navController = LocalNavController.current
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { navController.navigate(AboutRoute) }) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = stringResource(id = R.string.about_button_description)
                )
            }
            Text(
                text = stringResource(id = R.string.app_name),
                modifier = Modifier.align(Alignment.Center),
                style = TextStyle(
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            )

            IconButton(
                modifier = Modifier.align(Alignment.CenterEnd),
                onClick = { addWidget(context) }) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = stringResource(id = R.string.add_widget_button_description)
                )
            }
        }

        val uiState by viewModel.uiState.collectAsState()
        val pullRefreshState = rememberPullRefreshState(
            refreshing = uiState.refreshing,
            onRefresh = { viewModel.refreshUsersContributions() }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F),
                ) {
                    uiState.items.forEach { (userName, widgetId, config, items) ->
                        item {
                            ContributionWidget(
                                user = userName,
                                colors = items.asIntColors(),
                                config = config,
                                onClicked = {
                                    navController.navigate(
                                        UserRoute(
                                            user = userName,
                                            widgetId = widgetId,
                                        ),
                                    )
                                },
                                content = {
                                    Box(
                                        modifier = Modifier.background(MaterialTheme.colorScheme.background)
                                    ) {
                                        Text(
                                            text = userName,
                                            style = TextStyle(
                                                fontSize = 18.sp,
                                            ),
                                            modifier = Modifier.padding(4.dp)
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }

            if (uiState.items.isEmpty()) {
                TextButton(onClick = {
                    addWidget(context)
                }) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(id = R.string.add_widget_button_description)
                    )
                    Text(text = stringResource(id = R.string.add_widget))
                }
            }

            if (uiState.loading) {
                CircularProgressIndicator()
            }

            if (uiState.items.isNotEmpty()) {
                PullRefreshIndicator(
                    refreshing = uiState.refreshing,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}

private fun addWidget(context: Context) {
    val manager = AppWidgetManager.getInstance(context)

    if (manager.isRequestPinAppWidgetSupported) {
        manager.requestPinAppWidget(
            ComponentName(context, AppWidgetReceiver::class.java),
            null,
            PendingIntent.getBroadcast(
                context,
                0,
                Intent(context, AppWidgetReceiver::class.java).apply {
                    action = AppWidgetReceiver.ACTION_CONFIGURE_LATEST
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    } else {
        Toast.makeText(context, R.string.add_widget_on_home_screen, Toast.LENGTH_LONG).show()
    }
}
