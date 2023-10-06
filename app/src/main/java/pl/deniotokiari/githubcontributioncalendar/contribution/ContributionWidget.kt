package pl.deniotokiari.githubcontributioncalendar.contribution

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
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import pl.deniotokiari.githubcontributioncalendar.etc.BlocksBitmapCreator

@Composable
fun ContributionWidget(
    user: String,
    colors: IntArray,
    modifier: Modifier = Modifier,
    onClicked: ((String) -> Unit)? = null,
    content: (@Composable BoxScope.() -> Unit)? = null,
    blocksBitmapCreator: BlocksBitmapCreator = koinInject()
) {
    var size: IntSize by remember(user) { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
            .height(100.dp)
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
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val bitmap = blocksBitmapCreator(
                width = size.width,
                height = size.height,
                squareSize = 30,
                padding = 1,
                colors = colors
            )

            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "",
                modifier = Modifier.fillMaxSize()
            )

            content?.invoke(this)
        }
    }
}