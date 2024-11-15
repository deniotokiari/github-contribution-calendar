package pl.deniotokiari.githubcontributioncalendar.ui.compose

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.util.Consumer
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import pl.deniotokiari.githubcontributioncalendar.core.LocalNavController
import pl.deniotokiari.githubcontributioncalendar.ui.navigation.AboutRoute
import pl.deniotokiari.githubcontributioncalendar.ui.navigation.HomeRoute
import pl.deniotokiari.githubcontributioncalendar.ui.navigation.UserRoute
import pl.deniotokiari.githubcontributioncalendar.ui.theme.GitHubContributionCalendarTheme
import pl.deniotokiari.githubcontributioncalendar.ui.widget.AppWidget

@Composable
fun AppScreen() = GitHubContributionCalendarTheme {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        color = MaterialTheme.colorScheme.background,
    ) {
        val navController = rememberNavController()

        CompositionLocalProvider(LocalNavController provides navController) {
            NavHost(
                navController = navController,
                startDestination = HomeRoute,
            ) {
                composable<HomeRoute> { HomeScreen() }
                composable<UserRoute> { navBackStackEntry ->
                    val route = navBackStackEntry.toRoute<UserRoute>()

                    UserScreen(user = route.user, widgetId = route.widgetId)
                }
                composable<AboutRoute> { AboutScreen() }
            }
        }

        val context = (LocalContext.current as? ComponentActivity)

        handleNavigation(context?.intent, navController)

        DisposableEffect(Unit) {
            val listener = Consumer<Intent> { intent ->
                handleNavigation(intent, navController)
            }

            context?.addOnNewIntentListener(listener)

            onDispose {
                context?.removeOnNewIntentListener(listener)
            }
        }
    }
}

private fun handleNavigation(intent: Intent?, navHostController: NavHostController) {
    val user = intent?.extras?.getString(AppWidget.DESTINATION_USER)
    val widgetId = intent?.extras?.getInt(AppWidget.DESTINATION_WIDGET_ID)

    if (user != null && widgetId != null) {
        navHostController.navigate(
            route = UserRoute(user = user, widgetId = widgetId),
            navOptions = NavOptions
                .Builder()
                .apply {
                    setPopUpTo<HomeRoute>(inclusive = false)
                }
                .build(),
        )
    }
}
