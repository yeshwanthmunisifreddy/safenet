package com.thesubgraph.network.view.common

object DestinationRouteProtocol {
    sealed class Destination(val route: String, var arg: InputArgs? = null) {
        data object Home : Destination("main_screen")
    }
}