package pl.deniotokiari.githubcontributioncalendar.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pl.deniotokiari.githubcontributioncalendar.home.HomeScreen
import pl.deniotokiari.githubcontributioncalendar.ui.theme.GitHubContributionCalendarTheme

val LocalNavController = compositionLocalOf<NavHostController> { error("no default navController") }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GitHubContributionCalendarTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    CompositionLocalProvider(LocalNavController provides rememberNavController()) {
                        NavHost(
                            navController = LocalNavController.current,
                            startDestination = "home"
                        ) {
                            composable("home") { HomeScreen() }
                        }
                    }
                }
            }
        }
    }
}
