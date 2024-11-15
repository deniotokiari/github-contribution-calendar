package pl.deniotokiari.githubcontributioncalendar.core

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController

val LocalNavController = compositionLocalOf<NavHostController> { error("no default navController") }
