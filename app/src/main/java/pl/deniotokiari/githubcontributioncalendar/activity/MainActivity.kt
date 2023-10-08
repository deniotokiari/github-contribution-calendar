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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pl.deniotokiari.githubcontributioncalendar.home.HomeScreen
import pl.deniotokiari.githubcontributioncalendar.ui.theme.GitHubContributionCalendarTheme
import pl.deniotokiari.githubcontributioncalendar.user.UserScreen

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
                            composable(
                                "user/{user}/{widgetId}",
                                arguments = listOf(
                                    navArgument("user") { type = NavType.StringType },
                                    navArgument("widgetId") { type = NavType.IntType }
                                )
                            ) {
                                val user = requireNotNull(it.arguments?.getString("user"))
                                val widgetId = requireNotNull(it.arguments?.getInt("widgetId"))

                                UserScreen(user = user, widgetId = widgetId)
                            }
                        }
                    }
                }
            }
        }
    }
}
