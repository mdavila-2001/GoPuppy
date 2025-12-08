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
import com.mdavila_2001.gopuppy.ui.components.global.drawer.DrawerMenu
import com.mdavila_2001.gopuppy.ui.components.walker.EmptyStateMessage
import com.mdavila_2001.gopuppy.ui.components.walker.SectionHeader
import com.mdavila_2001.gopuppy.ui.components.walker.WalkerRequestActionCard
import com.mdavila_2001.gopuppy.ui.components.walker.WalkerStatusHeader
import com.mdavila_2001.gopuppy.ui.components.walker.WalkerWalkCard
import com.mdavila_2001.gopuppy.ui.theme.GoPuppyTheme
import com.mdavila_2001.gopuppy.ui.viewmodels.walker.WalkerHomeViewModel
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

    var showLogoutDialog by remember { mutableStateOf(false) }

    GoPuppyTheme(role = "walker") {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                DrawerMenu(
                    navController = navController,
                    isWalker = true,
                    userName = state.userName,
                    userPhotoUrl = state.userPhotoUrl,
                    onCloseDrawer = { scope.launch { drawerState.close() } },
                    onLogoutClick = {
                        showLogoutDialog = true
                    }
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
                        WalkerStatusHeader(
                            isAvailable = state.isAvailable,
                            onToggle = { isChecked ->
                                if (isChecked) {
                                    // Verificar si ya tenemos permisos
                                    val hasFine = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    ) == PackageManager.PERMISSION_GRANTED
                                    val hasCoarse = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    ) == PackageManager.PERMISSION_GRANTED

                                    if (hasFine || hasCoarse) {
                                        // Ya tenemos permisos, activar directamente
                                        viewModel.toggleAvailability(true)
                                    } else {
                                        // Solicitar permisos
                                        permissionLauncher.launch(
                                            arrayOf(
                                                Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION
                                            )
                                        )
                                    }
                                } else {
                                    // Desactivar disponibilidad
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
                            WalkerWalkCard(
                                walk = walk,
                                onStartWalk = {
                                    viewModel.startWalk(walk.id)
                                },
                                onEndWalk = {
                                    viewModel.endWalk(walk.id)
                                },
                                onViewDetails = {
                                    navController.navigate(
                                        NavRoutes.WalkDetail.createRoute(walk.id, true)
                                    )
                                }
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(32.dp)) }
                }
            }
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Cerrar Sesión") },
                text = { Text("¿Estás seguro de que deseas cerrar sesión?") },
                confirmButton = {
                    androidx.compose.material3.TextButton(
                        onClick = {
                            showLogoutDialog = false
                            viewModel.logout()
                            navController.navigate(NavRoutes.Onboarding.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    ) {
                        Text("Cerrar Sesión")
                    }
                },
                dismissButton = {
                    androidx.compose.material3.TextButton(
                        onClick = { showLogoutDialog = false }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}