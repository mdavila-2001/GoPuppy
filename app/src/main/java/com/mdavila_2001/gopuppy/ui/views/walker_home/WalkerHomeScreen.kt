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
import androidx.compose.ui.graphics.Color
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
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
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
                        Text("No tienes paseos programados", color = Color.Gray)
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
                        Text("No hay nuevas solicitudes", color = Color.Gray)
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
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("${walk.date}, ${walk.time}", color = Color(0xFFFF9800), fontWeight = FontWeight.Bold)
            Text("Paseo de ${walk.duration} min", fontWeight = FontWeight.Medium)
            Text(walk.address, color = Color.Gray)
            Spacer(Modifier.height(4.dp))
            Text("${walk.petName}, ${walk.petBreed}", fontWeight = FontWeight.Medium)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(onClick = { /* TODO: Iniciar paseo */ }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))) {
                    Text("Iniciar", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun WalkerRequestCard(request: WalkerRequestUiModel, onAccept: () -> Unit, onReject: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            // Imagen de la mascota (si tienes el recurso)
            // Icon(painter = painterResource(id = R.drawable.ic_pet), contentDescription = null)
            Column(Modifier.weight(1f)) {
                Text("${request.petName}, ${request.petBreed}", fontWeight = FontWeight.Bold)
                Text("${request.date}, ${request.time} - ${request.duration} min", color = Color.Gray)
            }
            Spacer(Modifier.width(8.dp))
            Button(onClick = onReject, colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)) {
                Text("Rechazar", color = Color.DarkGray)
            }
            Spacer(Modifier.width(8.dp))
            Button(onClick = onAccept, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))) {
                Text("Aceptar", color = Color.White)
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
