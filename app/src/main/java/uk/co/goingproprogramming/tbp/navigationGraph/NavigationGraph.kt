package uk.co.goingproprogramming.tbp.navigationGraph

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import uk.co.goingproprogramming.tbp.screens.brother.ScreenBrother
import uk.co.goingproprogramming.tbp.screens.home.ScreenHome
import uk.co.goingproprogramming.tbp.screens.zebra.ScreenZebra

sealed class Route(val routeName: String, val isInitialRoute: Boolean = false) {
    data object Home : Route("home", true)
    data object Zebra : Route("zebra")
    data object Brother : Route("brother")

    companion object {
        fun getInitialRoute(): Route =
            Route::class.sealedSubclasses
                .firstOrNull { it.objectInstance?.isInitialRoute == true }
                ?.objectInstance
                ?: Home
    }
}

fun String.toRoute(): Route =
    Route::class.sealedSubclasses
        .firstOrNull { it.objectInstance?.routeName == this }
        ?.objectInstance
        ?: Route.Home


val startDestination = Route.Home

@Composable
fun AppNavGraph(
    navController: NavHostController,
) {
    NavHost(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background),
        navController = navController,
        startDestination = startDestination.routeName
    ) {
        composable(Route.Home.routeName) { ScreenHome() }
        composable(Route.Zebra.routeName) { ScreenZebra() }
        composable(Route.Brother.routeName) { ScreenBrother() }
    }
}