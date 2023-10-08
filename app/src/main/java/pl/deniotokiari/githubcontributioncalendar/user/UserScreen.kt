package pl.deniotokiari.githubcontributioncalendar.user

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import pl.deniotokiari.githubcontributioncalendar.activity.LocalNavController
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "back button")
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
        ContributionWidget(user = uiState.user.user, colors = uiState.user.colors, config = uiState.config)

        Slider(
            value = uiState.config.blockSize.toFloat(),
            onValueChange = {
                viewModel.updateBlockSize(it.roundToInt())
            },
            valueRange = BlocksBitmapCreator.BLOCK_SIZE_MIN.toFloat()..BlocksBitmapCreator.BLOCK_SIZE_MAX.toFloat(),
            steps = BlocksBitmapCreator.BLOCK_SIZE_MAX - BlocksBitmapCreator.BLOCK_SIZE_MIN - 1
        )
        Text(text = "Block size ${uiState.config.blockSize}")

        // TODO padding
        // TODO transparency
    }
}
