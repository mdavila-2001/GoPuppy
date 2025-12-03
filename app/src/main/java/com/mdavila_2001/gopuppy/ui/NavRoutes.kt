package com.mdavila_2001.gopuppy.ui

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class NavRoutes(val route: String, val arguments: List<NamedNavArgument> = emptyList()) {
    object Splash : NavRoutes("splash")
    object Onboarding : NavRoutes("onboarding")
    object Login : NavRoutes(
        route = "login?isWalker={isWalker}",
        arguments = listOf(navArgument("isWalker") {
            type = NavType.BoolType
            defaultValue = false
    })
    ) {
        fun createRoute(isWalker: Boolean) = "login?isWalker=$isWalker"
    }

    object Register : NavRoutes("register/{isWalker}") {
        fun createRoute(isWalker: Boolean) = "register/$isWalker"
    }

    object OwnerHome : NavRoutes("owner_home")
    object MyPets : NavRoutes("my_pets")

    object PetForm : NavRoutes("pet_form?petId={petId}") {
        fun createRoute(petId: Int? = null) = "pet_form?petId=${petId ?: -1}"
    }

    object WalkerDetail : NavRoutes("walker_detail/{walkerId}") {
        fun createRoute(walkerId: Int) = "walker_detail/$walkerId"
    }

    object BookWalk : NavRoutes("book_walk/{walkerId}") {
        fun createRoute(walkerId: Int) = "book_walk/$walkerId"
    }

    object WalkerHome : NavRoutes("walker_home")
    object Requests : NavRoutes("walker_requests")
    object Schedule : NavRoutes("walker_schedule")

    object WalkDetail : NavRoutes("walk_detail/{walkId}") {
        fun createRoute(walkId: Int) = "walk_detail/$walkId"
    }
}