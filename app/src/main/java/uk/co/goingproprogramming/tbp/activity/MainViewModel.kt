package uk.co.goingproprogramming.tbp.activity

import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.co.goingproprogramming.tbp.services.IServiceNavigation
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val serviceNavigation: IServiceNavigation,
) : ViewModel() {

    private var navControllerSet = false

    fun setNavController(navController: NavHostController) {
        if (navControllerSet) {
            return
        }
        serviceNavigation.setNavController(navController)
        navControllerSet = true
    }
}