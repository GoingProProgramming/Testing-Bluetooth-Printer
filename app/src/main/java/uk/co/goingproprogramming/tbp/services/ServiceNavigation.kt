package uk.co.goingproprogramming.tbp.services

import androidx.navigation.NavController
import uk.co.goingproprogramming.tbp.navigationGraph.Route
import uk.co.goingproprogramming.tbp.navigationGraph.toRoute
import javax.inject.Inject

interface IServiceNavigation {
    /**
     * Setup the navigation service with the initial route
     */
    fun setNavController(
        navController: NavController,
    )

    /**
     * Method use to navigate to a new route
     */
    fun open(
        route: Route,
        removeCurrentFromStack: Boolean = false,
    )

    /**
     * Method to pop back programmatically
     */
    fun popBack()

    /**
     * Returns the current route
     */
    fun getCurrentRoute(): Route

    /**
     * Returns the initial route
     */
    fun getInitialRoute(): Route

    /**
     * Method when the back button is pressed to update the current
     */
    fun updateCurrentRoute()
}

class ServiceNavigation @Inject constructor() : IServiceNavigation {
    private lateinit var navController: NavController
    private lateinit var currentRoute: Route
    private lateinit var initialRoute: Route

    override fun setNavController(navController: NavController) {
        this.navController = navController
        val initialRoute = Route.getInitialRoute()
        this.currentRoute = initialRoute
        this.initialRoute = initialRoute
    }

    override fun open(route: Route, removeCurrentFromStack: Boolean) {
        if (route == currentRoute) {
            // preventing double tab
            return
        }
        navController.navigate(route.routeName) {
            if (removeCurrentFromStack) {
                popUpTo(currentRoute.routeName) {
                    inclusive = true
                }
            }
        }
        currentRoute = route
    }

    override fun popBack() {
        val previous = navController.previousBackStackEntry?.destination?.route
        if (previous == null || previous == currentRoute.routeName) {
            // preventing double tab
            return
        }

        navController.popBackStack()
        updateCurrentRoute()
    }

    override fun getCurrentRoute(): Route =
        currentRoute

    override fun getInitialRoute(): Route =
        initialRoute

    override fun updateCurrentRoute() {
        currentRoute = navController.currentDestination?.route?.toRoute() ?: Route.Home
    }
}