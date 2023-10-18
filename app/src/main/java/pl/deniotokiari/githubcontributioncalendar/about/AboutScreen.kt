package pl.deniotokiari.githubcontributioncalendar.about

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import pl.deniotokiari.githubcontributioncalendar.BuildConfig
import pl.deniotokiari.githubcontributioncalendar.R
import pl.deniotokiari.githubcontributioncalendar.activity.LocalNavController
import pl.deniotokiari.githubcontributioncalendar.ad.AddBanner
import pl.deniotokiari.githubcontributioncalendar.contribution.ContributionWidget
import pl.deniotokiari.githubcontributioncalendar.ui.theme.Pink40
import pl.deniotokiari.githubcontributioncalendar.ui.theme.Pink80
import pl.deniotokiari.githubcontributioncalendar.ui.theme.Purple40
import pl.deniotokiari.githubcontributioncalendar.ui.theme.Purple80
import pl.deniotokiari.githubcontributioncalendar.ui.theme.PurpleGrey40
import pl.deniotokiari.githubcontributioncalendar.ui.theme.PurpleGrey80
import pl.deniotokiari.githubcontributioncalendar.widget.WidgetConfiguration
import kotlin.random.Random

@Composable
fun AboutScreen(viewModel: AboutViewModel = koinViewModel()) {
    val navController = LocalNavController.current
    val colors = listOf(
        Purple80.toArgb(),
        PurpleGrey80.toArgb(),
        Pink80.toArgb(),
        Purple40.toArgb(),
        PurpleGrey40.toArgb(),
        Pink40.toArgb()
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
                text = stringResource(id = R.string.about),
                modifier = Modifier.align(Alignment.Center),
                style = TextStyle(
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            )
        }
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .weight(1F),
            ) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = stringResource(id = R.string.short_about)
                )

                ContributionWidget(
                    user = "",
                    colors = IntArray(100) { colors[Random.nextInt(colors.size)] },
                    config = WidgetConfiguration.default(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                )

                Text(
                    modifier = Modifier.padding(16.dp),
                    text = stringResource(id = R.string.features_description)
                )
            }

            Text(
                text = stringResource(id = R.string.version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                style = LocalTextStyle.current.copy(
                    color = PurpleGrey40
                )
            )

            AddBanner(
                modifier = Modifier.fillMaxWidth(),
                adUnitId = BuildConfig.ABOUT_SCREEN_AD_ID
            )
        }
    }
}