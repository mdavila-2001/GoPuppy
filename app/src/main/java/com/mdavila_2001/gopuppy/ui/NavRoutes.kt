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

    object Register : NavRoutes(
        route = "register/{isWalker}",
        arguments = listOf(navArgument("isWalker") {
            type = NavType.BoolType
            defaultValue = false
        })
    ) {
        fun createRoute(isWalker: Boolean) = "register/$isWalker"
    }

    object Addresses : NavRoutes("address")
    object AddressForm : NavRoutes("address_form")

    object OwnerHome : NavRoutes("owner_home")
    object MyPets : NavRoutes("my_pets")

    object PetForm : NavRoutes(
        route = "pet_form?petId={petId}",
        arguments = listOf(navArgument("petId") {
            type = NavType.StringType
            nullable = true
            defaultValue = null
        })
    ) {
        fun createRoute(petId: Int? = null) = "pet_form?petId=${petId ?: -1}"
    }

    object RequestWalk : NavRoutes("request_walk")
    object WalkerSearch : NavRoutes("walker_search")

    object WalkerDetail : NavRoutes("walker_detail/{walkerId}") {
        fun createRoute(walkerId: Int) = "walker_detail/$walkerId"
    }

    object BookWalk : NavRoutes("book_walk/{walkerId}") {
        fun createRoute(walkerId: Int) = "book_walk/$walkerId"
    }

    object WalkerHome : NavRoutes("walker_home")
    object Requests : NavRoutes("walker_requests")
    object Schedule : NavRoutes("walker_schedule")

    object WalkDetail : NavRoutes(
        route = "walk_detail/{walkId}?isWalker={isWalker}",
        arguments = listOf(
            navArgument("walkId") {
                type = NavType.IntType
            },
            navArgument("isWalker") {
                type = NavType.BoolType
                defaultValue = false
            }
        )
    ) {
        fun createRoute(walkId: Int, isWalker: Boolean = false) = "walk_detail/$walkId?isWalker=$isWalker"
    }

    object OwnerProfile : NavRoutes("owner_profile")
    object WalkerProfile : NavRoutes("walker_profile")
    object WalkHistory : NavRoutes("walk_history")
}