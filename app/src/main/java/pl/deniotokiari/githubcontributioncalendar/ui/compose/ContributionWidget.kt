package pl.deniotokiari.githubcontributioncalendar.ui.compose

import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import pl.deniotokiari.githubcontributioncalendar.core.successOrNull
import pl.deniotokiari.githubcontributioncalendar.data.datasource.AndroidBitmapDataSource
import pl.deniotokiari.githubcontributioncalendar.data.model.WidgetConfiguration
import pl.deniotokiari.githubcontributioncalendar.data.repository.AndroidBitmapRepository
import pl.deniotokiari.githubcontributioncalendar.data.repository.BitmapRepository

@Composable
fun ContributionWidget(
    user: String,
    colors: List<Int>,
    config: WidgetConfiguration,
    modifier: Modifier = Modifier,
    onClicked: ((String) -> Unit)? = null,
    content: (@Composable BoxScope.() -> Unit)? = null,
    bitmapRepository: BitmapRepository = koinInject()
) {
    var size: IntSize by remember(user) { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
            .height(110.dp)
            .onGloballyPositioned {
                if (it.size != IntSize.Zero) {
                    size = it.size
                }
            }
            .let {
                if (onClicked != null) {
                    it.clickable { onClicked(user) }
                } else {
                    it
                }
            }
    ) {
        if (size == IntSize.Zero || colors.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val bitmap = bitmapRepository.getBitmap(
                width = size.width,
                height = size.height,
                padding = config.padding.value,
                colors = colors,
                opacity = config.opacity.value,
                blockSize = config.blockSize.value
            ).successOrNull()

            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "blocks",
                    modifier = Modifier.fillMaxSize()
                )
            }

            content?.invoke(this)
        }
    }
}

@Preview(showBackground = false)
@Composable
fun ContributionWidgetLoadingPreview() = ContributionWidget(
    user = "deniotokiari",
    colors = listOf(),
    config = WidgetConfiguration.default(),
    modifier = Modifier,
    onClicked = {},
    content = {},
    bitmapRepository = AndroidBitmapRepository(AndroidBitmapDataSource())
)

@Preview(showBackground = false)
@Composable
fun ContributionWidgetColorsPreview() = Box {
    fun generate(): List<Int> = run {
        val list = mutableListOf<Int>()

        repeat(1000) {
            listOf(
                Color.BLACK,
                Color.DKGRAY,
                Color.GRAY,
                Color.LTGRAY,
                Color.WHITE,
                Color.RED,
                Color.GREEN,
                Color.BLUE,
                Color.YELLOW,
                Color.CYAN,
                Color.MAGENTA,
            ).random().let(list::add)
        }

        list
    }

    var colors by remember { mutableStateOf(generate()) }

    ContributionWidget(
        user = "deniotokiari",
        colors = colors,
        config = WidgetConfiguration.default(),
        modifier = Modifier,
        onClicked = {
            colors = generate()
        },
        content = {},
        bitmapRepository = AndroidBitmapRepository(AndroidBitmapDataSource())
    )
}
