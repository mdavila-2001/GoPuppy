package com.mdavila_2001.gopuppy.ui.views.walker_home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.mdavila_2001.gopuppy.ui.components.global.drawer.DrawerMenu
import com.mdavila_2001.gopuppy.ui.theme.GoPuppyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalkerHomeScreen(
    viewModel: WalkerHomeViewModel,
    navController: NavController
) {
    val state by viewModel.state.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    GoPuppyTheme(role = "walker") {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                DrawerMenu(
                    navController = navController,
                    isWalker = true,
                    onCloseDrawer = {
                        scope.launch {
                            drawerState.close()
                        }
                    }
                )
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Inicio",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menú"
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { /* TODO: Notificaciones */ }) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notificaciones"
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                    )
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Tus próximos paseos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    
                    if (state.upcomingWalks.isEmpty()) {
                        Text("No tienes paseos programados", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    } else {
                        state.upcomingWalks.forEach { walk ->
                            WalkerUpcomingWalkCard(
                                walk = walk,
                                onClick = {
                                    navController.navigate(
                                        com.mdavila_2001.gopuppy.ui.NavRoutes.WalkDetail.createRoute(
                                            walkId = walk.id,
                                            isWalker = true
                                        )
                                    )
                                }
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                    
                    Spacer(Modifier.height(24.dp))
                    
                    Text(
                        text = "Nuevas solicitudes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    
                    if (state.newRequests.isEmpty()) {
                        Text("No hay nuevas solicitudes", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    } else {
                        state.newRequests.forEach { request ->
                            WalkerRequestCard(
                                request = request,
                                onAccept = { viewModel.acceptRequest(request.id) },
                                onReject = { viewModel.rejectRequest(request.id) }
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WalkerUpcomingWalkCard(walk: WalkerWalkUiModel, onClick: () -> Unit = {}) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("${walk.date}, ${walk.time}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Text("Paseo de ${walk.duration} min", fontWeight = FontWeight.Medium)
            Text(walk.address, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            Spacer(Modifier.height(4.dp))
            Text("${walk.petName}, ${walk.petBreed}", fontWeight = FontWeight.Medium)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(onClick = { /* TODO: Iniciar paseo */ }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                    Text("Iniciar", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}

@Composable
fun WalkerRequestCard(request: WalkerRequestUiModel, onAccept: () -> Unit, onReject: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            // Imagen de la mascota (si tienes el recurso)
            // Icon(painter = painterResource(id = R.drawable.ic_pet), contentDescription = null)
            Column(Modifier.weight(1f)) {
                Text("${request.petName}, ${request.petBreed}", fontWeight = FontWeight.Bold)
                Text("${request.date}, ${request.time} - ${request.duration} min", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            Spacer(Modifier.width(8.dp))
            Button(onClick = onReject, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Text("Rechazar", color = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(Modifier.width(8.dp))
            Button(onClick = onAccept, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                Text("Aceptar", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

// Modelos UI

data class WalkerWalkUiModel(
    val id: Int,
    val date: String,
    val time: String,
    val duration: Int,
    val address: String,
    val petName: String,
    val petBreed: String
)

data class WalkerRequestUiModel(
    val id: Int,
    val date: String,
    val time: String,
    val duration: Int,
    val petName: String,
    val petBreed: String
)
