package com.mdavila_2001.gopuppy.ui.components.global

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mdavila_2001.gopuppy.ui.NavRoutes

@Composable
fun NavigationApp(modifier: Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.Splash.route
    ) {
        composable(NavRoutes.Splash.route) {
            PlaceholderScreen("Splash")
        }
        composable(NavRoutes.Landing.route) {
            PlaceholderScreen("Landing")
        }
        composable(NavRoutes.Login.route) {
            PlaceholderScreen("Login")
        }
        composable(NavRoutes.Register.route) {
            PlaceholderScreen("Register")
        }
        composable(NavRoutes.OwnerHome.route) {
            PlaceholderScreen("OwnerHome")
        }
        composable(NavRoutes.MyPets.route) {
            PlaceholderScreen("MyPets")
        }
        composable(NavRoutes.PetForm.route) {
            PlaceholderScreen("PetForm")
        }
        composable(NavRoutes.WalkerDetail.route) {
            PlaceholderScreen("WalkerDetail")
        }
        composable(NavRoutes.BookWalk.route) {
            PlaceholderScreen("BookWalk")
        }
        composable(NavRoutes.WalkerHome.route) {
            PlaceholderScreen("WalkerHome")
        }
        composable(NavRoutes.Requests.route) {
            PlaceholderScreen("Requests")
        }
        composable(NavRoutes.Schedule.route) {
            PlaceholderScreen("Schedule")
        }
        composable(NavRoutes.WalkDetail.route) {
            PlaceholderScreen("WalkDetail")
        }
    }
}

@Composable
fun PlaceholderScreen(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = text)
    }
}