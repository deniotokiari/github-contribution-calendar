package pl.deniotokiari.githubcontributioncalendar.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import pl.deniotokiari.githubcontributioncalendar.etc.BlocksBitmapCreator

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    blocksBitmapCreator: BlocksBitmapCreator = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
            ) {
                uiState.items.forEach { (user: String, items: IntArray) ->
                    item {
                        var size: IntSize by remember(user) { mutableStateOf(IntSize.Zero) }

                        Box(
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .fillMaxWidth()
                                .height(100.dp)
                                .onGloballyPositioned {
                                    if (it.size != IntSize.Zero) {
                                        size = it.size
                                    }
                                },
                        ) {
                            if (size == IntSize.Zero) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            } else {
                                Image(
                                    bitmap = blocksBitmapCreator(
                                        width = size.width,
                                        height = size.height,
                                        squareSize = 30,
                                        padding = 1,
                                        colors = items
                                    ).asImageBitmap(),
                                    contentDescription = "",
                                    modifier = Modifier.fillMaxSize()
                                )

                                Text(
                                    text = user,
                                    modifier = Modifier.padding(8.dp),
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        if (uiState.loading) {
            CircularProgressIndicator()
        }
    }
}