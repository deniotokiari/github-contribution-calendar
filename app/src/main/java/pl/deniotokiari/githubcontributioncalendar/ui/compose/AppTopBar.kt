package pl.deniotokiari.githubcontributioncalendar.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppTopBar(
    title: String,
    startAction: @Composable (BoxScope.() -> Unit)? = null,
    endAction: @Composable (BoxScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        startAction?.invoke(this)

        Text(
            text = title,
            modifier = Modifier.align(Alignment.Center),
            style = TextStyle(
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        )

        endAction?.invoke(this)
    }

    content()
}

@Preview(showBackground = true)
@Composable
fun AppTopBarPreview() = AppTopBar(
    title = "Title",
    startAction = {
        IconButton(onClick = { }) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
            )
        }
    },
    endAction = {
        IconButton(
            modifier = Modifier.align(Alignment.CenterEnd),
            onClick = { }) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null,
            )
        }
    },
    content = {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text("Content")
        }
    },
)
