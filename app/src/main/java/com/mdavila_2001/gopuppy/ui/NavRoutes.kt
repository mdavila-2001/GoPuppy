package com.mdavila_2001.gopuppy.ui

sealed class NavRoutes(val route: String) {
    object Splash : NavRoutes("splash")
    object Landing : NavRoutes("landing")
    object Login : NavRoutes("login")

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