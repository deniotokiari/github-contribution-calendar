package pl.deniotokiari.githubcontributioncalendar.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.util.Consumer
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pl.deniotokiari.githubcontributioncalendar.ui.compose.AboutScreen
import pl.deniotokiari.githubcontributioncalendar.ui.compose.HomeScreen
import pl.deniotokiari.githubcontributioncalendar.ui.compose.UserScreen
import pl.deniotokiari.githubcontributioncalendar.ui.theme.GitHubContributionCalendarTheme
import pl.deniotokiari.githubcontributioncalendar.ui.widget.AppWidget

val LocalNavController = compositionLocalOf<NavHostController> { error("no default navController") }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GitHubContributionCalendarTheme {
                Surface(
                    modifier = Modifier.fillMaxSize().padding(top = 16.dp),
                    color = MaterialTheme.colorScheme.background,
                ) {
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
                            composable("about") { AboutScreen() }
                        }

                        val navHostController = LocalNavController.current

                        handleNavigation(intent, navHostController)

                        DisposableEffect(LocalNavController.current) {
                            val listener = Consumer<Intent> {
                                handleNavigation(it, navHostController)
                            }

                            this@MainActivity.addOnNewIntentListener(listener)

                            onDispose { this@MainActivity.removeOnNewIntentListener(listener) }
                        }
                    }
                }
            }
        }
    }

    private fun handleNavigation(intent: Intent?, navHostController: NavHostController) {
        val user = intent?.extras?.getString(AppWidget.DESTINATION_USER)
        val widgetId = intent?.extras?.getInt(AppWidget.DESTINATION_WIDGET_ID)

        if (user != null && widgetId != null) {
            navHostController.popBackStack("home", inclusive = false)
            navHostController.navigate("user/${user}/${widgetId}")
        }
    }
}
