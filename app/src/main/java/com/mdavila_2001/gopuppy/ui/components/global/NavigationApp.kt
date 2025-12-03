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
import com.mdavila_2001.gopuppy.ui.views.LandingScreen
import com.mdavila_2001.gopuppy.ui.views.splash.SplashScreen
import com.mdavila_2001.gopuppy.ui.views.login.LoginScreen
import com.mdavila_2001.gopuppy.ui.views.register.RegisterScreen

@Composable
fun NavigationApp(modifier: Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.Splash.route,
    ) {
        composable(NavRoutes.Splash.route) {
            SplashScreen(navController)
        }
        composable(NavRoutes.Landing.route) {
            LandingScreen(navController)
        }
        composable(
            route = NavRoutes.Login.route,
            arguments = NavRoutes.Login.arguments
        ) { backStackEntry ->
            val isWalker = backStackEntry.arguments?.getBoolean("isWalker") ?: false
            LoginScreen(navController, isWalker)
        }
        composable(
            route = NavRoutes.Register.route,
            arguments = NavRoutes.Register.arguments
        ) { backStackEntry ->
            val isWalker = backStackEntry.arguments?.getBoolean("isWalker") ?: false
            RegisterScreen(navController, isWalker)
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