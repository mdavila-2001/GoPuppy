package com.mdavila_2001.gopuppy.ui.views.walker_reviews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.mdavila_2001.gopuppy.ui.components.global.AppBar
import com.mdavila_2001.gopuppy.ui.components.global.dialogs.ConfirmDialog
import com.mdavila_2001.gopuppy.ui.components.global.drawer.DrawerMenu
import com.mdavila_2001.gopuppy.ui.components.walker.EmptyStateMessage
import com.mdavila_2001.gopuppy.ui.theme.GoPuppyTheme
import com.mdavila_2001.gopuppy.ui.viewmodels.walker.WalkerReviewsViewModel
import java.util.Locale
import kotlinx.coroutines.launch

data class ReviewUiModel(
    val id: Int,
    val userName: String,
    val userPhotoUrl: String?,
    val petName: String,
    val rating: Int,
    val comment: String,
    val timeAgo: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalkerReviewsScreen(
    navController: NavController,
    viewModel: WalkerReviewsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("Más recientes") }

    LaunchedEffect(Unit) {
        viewModel.loadReviews()
    }

    GoPuppyTheme(role = "walker") {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                DrawerMenu(
                    navController = navController,
                    isWalker = true,
                    userName = state.walkerName,
                    userPhotoUrl = state.walkerPhotoUrl,
                    onCloseDrawer = {
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    onLogoutClick = {
                        showLogoutDialog = true
                    }
                )
            }
        ) {
            Scaffold(
                topBar = {
                    AppBar(
                        title = "Mis Calificaciones",
                        logOutEnabled = false,
                        backEnabled = true,
                        onLogoutClick = { },
                        onBackClick = { navController.navigateUp() },
                        modifier = Modifier
                    )
                }
            ) { paddingValues ->
                if (state.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            RatingHeaderCard(
                                averageRating = state.averageRating,
                                totalReviews = state.reviews.size,
                                ratingDistribution = state.ratingDistribution
                            )
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = selectedFilter == "Más recientes",
                                    onClick = { selectedFilter = "Más recientes" },
                                    label = { Text("Más recientes") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                )
                                FilterChip(
                                    selected = selectedFilter == "Mejor calificación",
                                    onClick = { selectedFilter = "Mejor calificación" },
                                    label = { Text("Mejor calificación") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                )
                                FilterChip(
                                    selected = selectedFilter == "Peor calificación",
                                    onClick = { selectedFilter = "Peor calificación" },
                                    label = { Text("Peor calif.") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                )
                            }
                        }

                        val filteredReviews = when (selectedFilter) {
                            "Mejor calificación" -> state.reviews.sortedByDescending { it.rating }
                            "Peor calificación" -> state.reviews.sortedBy { it.rating }
                            else -> state.reviews
                        }
                        items(filteredReviews) { review ->
                            ReviewCard(review = review)
                        }

                        if (state.reviews.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 48.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    EmptyStateMessage(
                                        "Aún no tienes calificaciones"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showLogoutDialog) {
            ConfirmDialog(
                title = "Cerrar Sesión",
                message = "¿Estás seguro que deseas cerrar sesión?",
                onConfirm = {
                    viewModel.logout {
                        navController.navigate("onboarding") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                    showLogoutDialog = false
                },
                onDismiss = { showLogoutDialog = false }
            )
        }
    }
}

@Composable
private fun RatingHeaderCard(
    averageRating: Double,
    totalReviews: Int,
    ratingDistribution: Map<Int, Int>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = String.format(Locale.getDefault(), "%.1f", averageRating),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(5) { index ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (index < averageRating.toInt()) Color(0xFFFFB800) else Color.LightGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Basado en $totalReviews reseñas",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (rating in 5 downTo 1) {
                    val count = ratingDistribution[rating] ?: 0
                    val percentage = if (totalReviews > 0) (count.toFloat() / totalReviews) else 0f

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "$rating",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.width(12.dp)
                        )
                        LinearProgressIndicator(
                            progress = { percentage },
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Text(
                            text = "${(percentage * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.width(40.dp),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewCard(review: ReviewUiModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val ownerInitial = review.userName.firstOrNull()?.toString()?.uppercase() ?: "?"
                if (!review.userPhotoUrl.isNullOrBlank() && review.userPhotoUrl != "null") {
                    AsyncImage(
                        model = review.userPhotoUrl,
                        contentDescription = "Foto de ${review.userName}",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = ownerInitial,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${review.userName} (dueño de ${review.petName})",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Hace ${review.timeAgo}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                repeat(5) { index ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (index < review.rating) Color(0xFFFFB800) else Color.LightGray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                lineHeight = 20.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WalkerReviewsScreenPreview() {
    GoPuppyTheme(role = "walker") {
        WalkerReviewsScreen(navController = rememberNavController())
    }
}
