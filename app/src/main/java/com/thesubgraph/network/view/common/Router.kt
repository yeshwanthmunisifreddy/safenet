package com.thesubgraph.network.view.common

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.thesubgraph.network.MainActivity
import com.thesubgraph.network.view.HomeScreen
import com.thesubgraph.network.viewmodel.screens.HomeViewModel

class Router(
    val context: Context? = null,
    val navController: NavHostController? = null,
    val activity: MainActivity?,
) {
    @Composable
    fun ComposeRouter(destination: String) {
        val controller = remember { navController }
        controller?.let {
            NavHost(
                navController = controller,
                startDestination = destination,
            ) {
                composable(route = DestinationRouteProtocol.Destination.Home.route) {
                    activity?.window?.let { WindowCompat.setDecorFitsSystemWindows(it, false) }
                    val viewModel: HomeViewModel = hiltViewModel()
                    HomeScreen(route = this@Router, viewModel = viewModel)
                }
            }
        }
    }

    fun navigateTo(
        destination: DestinationRouteProtocol.Destination,
        mInclusive: Boolean,
        args: InputArgs? = null
    ) {
        navController?.navigate(destination.route) {
            popUpTo(navController.graph.id) {
                inclusive = mInclusive
            }
        }
    }

    fun popBackStack() {
        navController?.popBackStack()
    }

    fun getCurrentRoute(): String {
        return navController?.currentBackStackEntry?.destination?.route ?: ""
    }

    fun closeActivity() {
        activity?.finish()
    }
}