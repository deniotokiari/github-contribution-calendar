package pl.deniotokiari.githubcontributioncalendar.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.pullrefresh.PullRefreshIndicator
import androidx.compose.material3.pullrefresh.pullRefresh
import androidx.compose.material3.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import pl.deniotokiari.githubcontributioncalendar.R
import pl.deniotokiari.githubcontributioncalendar.data.model.WidgetConfiguration
import pl.deniotokiari.githubcontributioncalendar.ui.activity.LocalNavController
import pl.deniotokiari.githubcontributioncalendar.ui.viewmodel.UserViewModel
import kotlin.math.roundToInt

@Composable
fun UserScreen(
    user: String,
    widgetId: Int,
    viewModel: UserViewModel = koinViewModel(parameters = { parametersOf(user, widgetId) })
) {
    val uiState by viewModel.uiState.collectAsState()
    val navController = LocalNavController.current
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.refreshing,
        onRefresh = { viewModel.refreshUserContribution() }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.back_button_description)
                )
            }
            Text(
                text = uiState.userName.value,
                modifier = Modifier.align(Alignment.Center),
                style = TextStyle(
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            )
        }

        Box(
            modifier = Modifier
                .pullRefresh(pullRefreshState)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F),
                ) {
                    item {
                        ContributionWidget(
                            user = uiState.userName.value,
                            colors = uiState.contributions.asIntColors(),
                            config = uiState.config
                        )

                        Text(text = stringResource(id = R.string.block_size), modifier = Modifier.padding(8.dp))
                        Slider(
                            value = uiState.config.blockSize.value.toFloat(),
                            onValueChange = {
                                viewModel.updateBlockSize(it.roundToInt())
                            },
                            valueRange = WidgetConfiguration.BLOCK_SIZE_MIN.toFloat()..WidgetConfiguration.BLOCK_SIZE_MAX.toFloat(),
                            steps = WidgetConfiguration.BLOCK_SIZE_MAX - WidgetConfiguration.BLOCK_SIZE_MIN - 1
                        )

                        Text(text = stringResource(id = R.string.padding), modifier = Modifier.padding(8.dp))
                        Slider(
                            value = uiState.config.padding.value.toFloat(),
                            onValueChange = {
                                viewModel.updatePadding(it.roundToInt())
                            },
                            valueRange = WidgetConfiguration.PADDING_MIN.toFloat()..WidgetConfiguration.PADDING_MAX.toFloat(),
                            steps = WidgetConfiguration.PADDING_MAX - WidgetConfiguration.PADDING_MIN - 1
                        )

                        Text(text = stringResource(id = R.string.opacity), modifier = Modifier.padding(8.dp))
                        Slider(
                            value = uiState.config.opacity.value.toFloat(),
                            onValueChange = {
                                viewModel.updateOpacity(it.roundToInt())
                            },
                            valueRange = WidgetConfiguration.OPACITY_MIN.toFloat()..WidgetConfiguration.OPACITY_MAX.toFloat(),
                            steps = WidgetConfiguration.OPACITY_MAX - WidgetConfiguration.OPACITY_MIN - 1
                        )
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = uiState.refreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}
