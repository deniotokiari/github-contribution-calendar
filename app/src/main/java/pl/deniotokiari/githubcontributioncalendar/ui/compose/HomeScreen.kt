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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import pl.deniotokiari.githubcontributioncalendar.R
import pl.deniotokiari.githubcontributioncalendar.core.LocalNavController
import pl.deniotokiari.githubcontributioncalendar.ui.navigation.AboutRoute
import pl.deniotokiari.githubcontributioncalendar.ui.navigation.UserRoute
import pl.deniotokiari.githubcontributioncalendar.ui.viewmodel.HomeViewModel
import pl.deniotokiari.githubcontributioncalendar.ui.viewmodel.HomeViewModel.UiState.Empty.items
import pl.deniotokiari.githubcontributioncalendar.ui.widget.AppWidgetReceiver

@Composable
fun HomeScreen() {
    val viewModel: HomeViewModel = koinViewModel()
    val navController = LocalNavController.current
    val context = LocalContext.current


    val uiState by viewModel.uiState.collectAsState()

    HomeContent(
        uiState = uiState,
        onAddWidgetClick = { addWidget(context) },
        onStartActionClick = { navController.navigate(AboutRoute) },
        onEndActionClick = { addWidget(context) },
        onRefresh = { viewModel.refreshUsersContributions() },
        onWidgetClick = { user, widgetId ->
            navController.navigate(UserRoute(user = user, widgetId = widgetId))
        }
    )
}

@Composable
private fun HomeEmpty(
    onAddWidgetClick: () -> Unit,
) = TextButton(
    onClick = { onAddWidgetClick() },
) {
    Icon(
        imageVector = Icons.Filled.Add,
        contentDescription = stringResource(id = R.string.add_widget_button_description)
    )

    Text(text = stringResource(id = R.string.add_widget))
}

@Composable
private fun HomeItems(
    uiState: HomeViewModel.UiState.Content,
    onRefresh: () -> Unit,
    onWidgetClick: (String, Int) -> Unit,
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.refreshing,
        onRefresh = { onRefresh() }
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
                                onWidgetClick(userName, widgetId)
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

        if (uiState.items.isNotEmpty()) {
            PullRefreshIndicator(
                refreshing = uiState.refreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
private fun HomeContent(
    uiState: HomeViewModel.UiState,
    onAddWidgetClick: () -> Unit,
    onStartActionClick: () -> Unit,
    onEndActionClick: () -> Unit,
    onRefresh: () -> Unit,
    onWidgetClick: (String, Int) -> Unit,
) = AppTopBar(
    title = stringResource(id = R.string.app_name),
    startAction = {
        IconButton(onClick = { onStartActionClick() }) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = stringResource(id = R.string.about_button_description),
            )
        }
    },
    endAction = {
        IconButton(
            modifier = Modifier.align(Alignment.CenterEnd),
            onClick = { onEndActionClick() }) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = stringResource(id = R.string.add_widget_button_description),
            )
        }
    },
) {
    when (uiState) {
        is HomeViewModel.UiState.Content -> HomeItems(
            uiState = uiState,
            onRefresh = onRefresh,
            onWidgetClick = onWidgetClick,
        )

        HomeViewModel.UiState.Empty -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            HomeEmpty(onAddWidgetClick = onAddWidgetClick)
        }

        HomeViewModel.UiState.Loading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {CircularProgressIndicator()}
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

@Preview(showBackground = true)
@Composable
private fun HomeScreenLoadingPreview() = HomeContent(
    uiState = HomeViewModel.UiState.Loading,
    onAddWidgetClick = {},
    onStartActionClick = {},
    onEndActionClick = {},
    onRefresh = {},
    onWidgetClick = { _, _ -> }
)

@Preview(showBackground = true)
@Composable
private fun HomeScreenEmptyPreview() = HomeContent(
    uiState = HomeViewModel.UiState.Empty,
    onAddWidgetClick = {},
    onStartActionClick = {},
    onEndActionClick = {},
    onRefresh = {},
    onWidgetClick = { _, _ -> }
)
