package com.mdavila_2001.gopuppy.ui.components.global

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mdavila_2001.gopuppy.ui.NavRoutes
import com.mdavila_2001.gopuppy.ui.views.OnboardingScreen
import com.mdavila_2001.gopuppy.ui.views.address.AddressListScreen
import com.mdavila_2001.gopuppy.ui.views.address_form.AddressFormScreen
import com.mdavila_2001.gopuppy.ui.views.splash.SplashScreen
import com.mdavila_2001.gopuppy.ui.views.login.LoginScreen
import com.mdavila_2001.gopuppy.ui.views.register.RegisterScreen
import com.mdavila_2001.gopuppy.ui.views.owner_home.OwnerHomeScreen
import com.mdavila_2001.gopuppy.ui.views.pet_form.PetFormScreen
import com.mdavila_2001.gopuppy.ui.views.requestwalk.RequestWalkScreen
import com.mdavila_2001.gopuppy.ui.views.walker_home.WalkerHomeScreen
import com.mdavila_2001.gopuppy.ui.views.walker_search.WalkerSearchScreen
import com.mdavila_2001.gopuppy.ui.views.walker_search.WalkerSearchViewModel

@Composable
fun NavigationApp(modifier: Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.Splash.route
    ) {
        composable(NavRoutes.Splash.route) {
            SplashScreen(navController)
        }
        composable(NavRoutes.Onboarding.route) {
            OnboardingScreen(navController)
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
        // WelcomeTransition removido - navegación directa al Home
        composable(NavRoutes.OwnerHome.route) {
            OwnerHomeScreen(navController)
        }
        composable(NavRoutes.MyPets.route) {
            PlaceholderScreen("MyPets")
        }
        composable(
            route = NavRoutes.PetForm.route,
            arguments = NavRoutes.PetForm.arguments
        ) { backStackEntry ->
            val petIdString = backStackEntry.arguments?.getString("petId")
            val petId = petIdString?.toIntOrNull()?.takeIf { it > 0 }
            PetFormScreen(navController, petId)
        }
        composable(NavRoutes.RequestWalk.route) {
            RequestWalkScreen(navController = navController)
        }
        composable(NavRoutes.WalkerSearch.route) {
            WalkerSearchScreen(
                viewModel = viewModel(),
                navController = navController
            )
        }
        composable(NavRoutes.WalkerDetail.route) {
            PlaceholderScreen("WalkerDetail")
        }
        composable(NavRoutes.BookWalk.route) {
            PlaceholderScreen("BookWalk")
        }
        composable(NavRoutes.WalkerHome.route) {
            WalkerHomeScreen(navController = navController)
        }
        composable(NavRoutes.Addresses.route) {
            AddressListScreen(navController = navController)
        }
        composable(NavRoutes.AddressForm.route) {
            AddressFormScreen(navController = navController)
        }
        composable(NavRoutes.Requests.route) {
            PlaceholderScreen("Requests")
        }
        composable(NavRoutes.Schedule.route) {
            PlaceholderScreen("Schedule")
        }
        composable(
            route = NavRoutes.WalkDetail.route,
            arguments = NavRoutes.WalkDetail.arguments
        ) { backStackEntry ->
            val walkId = backStackEntry.arguments?.getInt("walkId") ?: 0
            val isWalker = backStackEntry.arguments?.getBoolean("isWalker") ?: false
            com.mdavila_2001.gopuppy.ui.views.walk_detail.WalkDetailScreen(
                navController = navController,
                walkId = walkId,
                isWalker = isWalker
            )
        }
        composable(NavRoutes.OwnerProfile.route) {
            com.mdavila_2001.gopuppy.ui.views.owner_profile.OwnerProfileScreen(navController)
        }
        composable(NavRoutes.WalkerProfile.route) {
            com.mdavila_2001.gopuppy.ui.views.walker_profile.WalkerProfileScreen(navController)
        }
        composable(NavRoutes.WalkHistory.route) {
            com.mdavila_2001.gopuppy.ui.views.walk_history.WalkHistoryScreen(navController)
        }
    }
}

@Composable
fun PlaceholderScreen(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = text)
    }
}