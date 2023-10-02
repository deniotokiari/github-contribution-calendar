package pl.deniotokiari.githubcontributioncalendar

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.lazy.GridCells
import androidx.glance.appwidget.lazy.LazyVerticalGrid
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.deniotokiari.githubcontributioncalendar.service.github.QueryUserContributionQuery

class AppWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Exact

    private var isSized = false

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val result = withContext(Dispatchers.IO) {
            val response = apolloClient.query(QueryUserContributionQuery("deniotokiari")).execute()

            response
                .data
                ?.user
                ?.contributionsCollection
                ?.contributionCalendar
                ?.weeks
                ?.flatMap { it.contributionDays }
                ?.map { Color(it.color.toColorInt()) }

        } ?: emptyList()

        provideContent {
            if (!isSized) {
                isSized = true
            } else {
                isSized = false

                return@provideContent
            }

            val size = LocalSize.current
            val squareSize = 20.dp
            val minWidth = squareSize + 3.dp
            val padding = 4.dp
            val countRows = ((size.width - padding) / minWidth).toInt()
            val countColumns = ((size.height - padding) / squareSize).toInt()
            val sizeToTrim = result.size - countRows * countColumns
            val colorsToUse = result.drop(sizeToTrim)

            LazyVerticalGrid(
                gridCells = GridCells.Adaptive((minWidth)),
                modifier = GlanceModifier.fillMaxSize().padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                repeat((countRows) * countColumns) {
                    item {
                        Box(modifier = GlanceModifier.size(squareSize).padding(2.dp)) {
                            Spacer(
                                modifier = GlanceModifier.background(colorsToUse[it]).fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}