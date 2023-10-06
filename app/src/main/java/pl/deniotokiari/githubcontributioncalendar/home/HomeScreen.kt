package pl.deniotokiari.githubcontributioncalendar.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import pl.deniotokiari.githubcontributioncalendar.activity.LocalNavController
import pl.deniotokiari.githubcontributioncalendar.contribution.ContributionWidget

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val navController = LocalNavController.current

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
                        ContributionWidget(
                            user = user,
                            colors = items,
                            onClicked = {
                                navController.navigate("user/${user}")
                            }
                        )
                    }
                }
            }
        }

        if (uiState.loading) {
            CircularProgressIndicator()
        }
    }
}