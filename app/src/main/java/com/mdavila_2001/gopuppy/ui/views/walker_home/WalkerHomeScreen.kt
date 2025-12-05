package com.mdavila_2001.gopuppy.ui.views.walker_home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mdavila_2001.gopuppy.ui.NavRoutes
import com.mdavila_2001.gopuppy.ui.components.global.cards.WalkCard
import com.mdavila_2001.gopuppy.ui.components.global.drawer.DrawerMenu
import com.mdavila_2001.gopuppy.ui.components.walker.AvailabilityCard
import com.mdavila_2001.gopuppy.ui.components.walker.EmptyStateMessage
import com.mdavila_2001.gopuppy.ui.components.walker.SectionHeader
import com.mdavila_2001.gopuppy.ui.components.walker.WalkerRequestActionCard
import com.mdavila_2001.gopuppy.ui.theme.GoPuppyTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalkerHomeScreen(
    viewModel: WalkerHomeViewModel = viewModel(),
    navController: NavController
) {
    val state by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (locationGranted) {
            viewModel.toggleAvailability(true)
        } else {
            // Si no se concede el permiso, asegurarse de que el paseador no esté disponible
            viewModel.toggleAvailability(false)
        }
    }

    GoPuppyTheme(role = "walker") {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                DrawerMenu(
                    navController = navController,
                    isWalker = true,
                    onCloseDrawer = { scope.launch { drawerState.close() } }
                )
            }
        ) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                         title = { Text("Panel de Paseador", fontWeight = FontWeight.Bold) },
                         navigationIcon = {
                             IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                 Icon(Icons.Default.Menu, "Menú")
                             }
                         },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                     )
                 }
             ) { innerPadding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        AvailabilityCard(
                            isAvailable = state.isAvailable,
                            onToggle = { isChecked ->
                                if (isChecked) {
                                    val hasFine = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    ) == PackageManager.PERMISSION_GRANTED
                                    val hasCoarse = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    ) == PackageManager.PERMISSION_GRANTED

                                    if (hasFine || hasCoarse) {
                                        viewModel.toggleAvailability(true)
                                    } else {
                                        // No tiene permiso, lo pedimos
                                        permissionLauncher.launch(
                                            arrayOf(
                                                Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                                Manifest.permission.POST_NOTIFICATIONS // Para Android 13+
                                            )
                                        )
                                    }
                                } else {
                                    viewModel.toggleAvailability(false)
                                }
                            },
                            isLoading = state.isLoading
                        )
                    }

                    item {
                        SectionHeader("Solicitudes Pendientes (${state.newRequests.size})")
                    }

                    if (state.newRequests.isEmpty()) {
                        item { EmptyStateMessage("No tienes nuevas solicitudes.") }
                    } else {
                        items(state.newRequests) { walk ->
                            WalkerRequestActionCard(
                                walk = walk,
                                onAccept = { viewModel.acceptRequest(walk.id) },
                                onReject = { viewModel.rejectRequest(walk.id) }
                            )
                        }
                    }

                    item {
                        SectionHeader("Tu Agenda")
                    }

                    if (state.upcomingWalks.isEmpty()) {
                        item { EmptyStateMessage("Tu agenda está libre por ahora.") }
                    } else {
                        items(state.upcomingWalks) { walk ->
                            WalkCard(
                                petName = walk.pet.name,
                                date = walk.scheduledAt,
                                time = "${walk.durationMinutes} min",
                                duration = "",
                                status = walk.status,
                                price = null,
                                onClick = {
                                    navController.navigate(
                                        NavRoutes.WalkDetail.createRoute(walk.id)
                                    )
                                }
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(32.dp)) }
                }
            }
        }
    }
}