package com.denchic45.binlist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.denchic45.binlist.ui.History
import com.denchic45.binlist.ui.Input
import com.denchic45.binlist.ui.history.HistoryScreen
import com.denchic45.binlist.ui.input.BinInputScreen
import com.denchic45.binlist.ui.theme.BinlistAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BinlistAppTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Input
                ) {
                    composable<Input> {
                        BinInputScreen(navigateToHistory = {
                            navController.navigate(History)
                        })
                    }
                    composable<History> {
                        HistoryScreen(navigationBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BinlistAppTheme {
        Greeting("Android")
    }
}