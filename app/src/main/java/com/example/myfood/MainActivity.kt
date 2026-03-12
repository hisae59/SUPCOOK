package com.example.myfood

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.myfood.ui.home.HomeScreen
import com.example.myfood.ui.theme.MyFoodTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyFoodTheme {
                AppNavigation()
            }
        }
    }
}

// Navigation simple par variable d'état
sealed class Screen {
    object Home : Screen()
    data class Detail(val mealId: String) : Screen()
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

    when (val screen = currentScreen) {
        is Screen.Home -> {
            HomeScreen(onMealClick = { id -> currentScreen = Screen.Detail(id) })
        }
        is Screen.Detail -> {
            // TODO : DetailScreen sera ajouté dans la prochaine étape
            HomeScreen(onMealClick = { id -> currentScreen = Screen.Detail(id) })
        }
    }
}
