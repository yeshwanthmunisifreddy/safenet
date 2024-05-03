package com.thesubgraph.network

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.thesubgraph.network.ui.theme.NetworkTheme
import com.thesubgraph.network.view.common.DestinationRouteProtocol
import com.thesubgraph.network.view.common.Router
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NetworkTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NetworkApp(
                        applicationContext = applicationContext,
                        mainActivity = this@MainActivity
                    )
                }
            }
        }
    }
}
@Composable
fun NetworkApp(applicationContext: Context, mainActivity: MainActivity) {
    val navController = rememberNavController()
    val router = Router(
        context = applicationContext,
        navController = navController,
        activity = mainActivity,
    )
    router.ComposeRouter(DestinationRouteProtocol.Destination.Home.route)
}
