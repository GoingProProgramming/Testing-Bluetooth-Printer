package uk.co.goingproprogramming.tbp.navigationGraph

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import uk.co.goingproprogramming.tbp.screens.bixolon.ScreenBixolon
import uk.co.goingproprogramming.tbp.screens.brother.ScreenBrother
import uk.co.goingproprogramming.tbp.screens.discoverBixolon.ScreenDiscoverBixolon
import uk.co.goingproprogramming.tbp.screens.discoverBrother.ScreenDiscoverBrother
import uk.co.goingproprogramming.tbp.screens.discoverZebra.ScreenDiscoverZebra
import uk.co.goingproprogramming.tbp.screens.home.ScreenHome
import uk.co.goingproprogramming.tbp.screens.zebra.ScreenZebra

sealed class Route(val routeName: String, val isInitialRoute: Boolean = false) {
    data object Home : Route("home", true)
    data object DiscoverZebra : Route("discoverZebra")
    data object Zebra : Route("zebra")
    data object DiscoverBrother : Route("discoverBrother")
    data object Brother : Route("brother")
    data object DiscoverBixolon : Route("discoverBixolon")
    data object Bixolon : Route("bixolon")

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
        composable(Route.DiscoverZebra.routeName) { ScreenDiscoverZebra() }
        composable(Route.Zebra.routeName) { ScreenZebra() }
        composable(Route.DiscoverBrother.routeName) { ScreenDiscoverBrother() }
        composable(Route.Brother.routeName) { ScreenBrother() }
        composable(Route.DiscoverBixolon.routeName) { ScreenDiscoverBixolon() }
        composable(Route.Bixolon.routeName) { ScreenBixolon() }
    }
}