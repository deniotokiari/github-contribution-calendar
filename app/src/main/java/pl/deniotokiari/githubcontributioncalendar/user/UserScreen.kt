package pl.deniotokiari.githubcontributioncalendar.user

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
import pl.deniotokiari.githubcontributioncalendar.BuildConfig
import pl.deniotokiari.githubcontributioncalendar.R
import pl.deniotokiari.githubcontributioncalendar.activity.LocalNavController
import pl.deniotokiari.githubcontributioncalendar.ad.AddBanner
import pl.deniotokiari.githubcontributioncalendar.contribution.ContributionWidget
import pl.deniotokiari.githubcontributioncalendar.etc.BlocksBitmapCreator
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
                text = uiState.user.user,
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
                            user = uiState.user.user,
                            colors = uiState.user.colors,
                            config = uiState.config
                        )

                        Slider(
                            value = uiState.config.blockSize.toFloat(),
                            onValueChange = {
                                viewModel.updateBlockSize(it.roundToInt())
                            },
                            valueRange = BlocksBitmapCreator.BLOCK_SIZE_MIN.toFloat()..BlocksBitmapCreator.BLOCK_SIZE_MAX.toFloat(),
                            steps = BlocksBitmapCreator.BLOCK_SIZE_MAX - BlocksBitmapCreator.BLOCK_SIZE_MIN - 1
                        )
                        Text(text = "Block size ${uiState.config.blockSize}", modifier = Modifier.padding(8.dp))

                        Slider(
                            value = uiState.config.padding.toFloat(),
                            onValueChange = {
                                viewModel.updatePadding(it.roundToInt())
                            },
                            valueRange = BlocksBitmapCreator.PADDING_MIN.toFloat()..BlocksBitmapCreator.PADDING_MAX.toFloat(),
                            steps = BlocksBitmapCreator.PADDING_MAX - BlocksBitmapCreator.PADDING_MIN - 1
                        )
                        Text(text = "Padding ${uiState.config.padding}", modifier = Modifier.padding(8.dp))

                        Slider(
                            value = uiState.config.opacity.toFloat(),
                            onValueChange = {
                                viewModel.updateOpacity(it.roundToInt())
                            },
                            valueRange = BlocksBitmapCreator.OPACITY_MIN.toFloat()..BlocksBitmapCreator.OPACITY_MAX.toFloat(),
                            steps = BlocksBitmapCreator.OPACITY_MAX - BlocksBitmapCreator.OPACITY_MIN - 1
                        )
                        Text(text = "Opacity ${uiState.config.opacity}", modifier = Modifier.padding(8.dp))
                    }
                }

                AddBanner(
                    modifier = Modifier.fillMaxWidth(),
                    adUnitId = BuildConfig.USER_SCREEN_AD_ID
                )
            }

            PullRefreshIndicator(
                refreshing = uiState.refreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}
