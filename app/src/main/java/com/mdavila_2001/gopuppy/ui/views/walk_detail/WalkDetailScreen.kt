package com.mdavila_2001.gopuppy.ui.views.walk_detail

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.mdavila_2001.gopuppy.ui.FileUtils
import com.mdavila_2001.gopuppy.ui.components.global.AppBar
import com.mdavila_2001.gopuppy.ui.components.global.StatusChip
import com.mdavila_2001.gopuppy.ui.components.global.buttons.Button
import com.mdavila_2001.gopuppy.ui.components.global.buttons.DangerButton
import com.mdavila_2001.gopuppy.ui.components.global.buttons.OutlinedButton
import com.mdavila_2001.gopuppy.ui.components.global.drawer.DrawerMenu
import com.mdavila_2001.gopuppy.ui.components.walk.PetInfoSection
import com.mdavila_2001.gopuppy.ui.components.walk.WalkDetailsSection
import com.mdavila_2001.gopuppy.ui.components.walk.WalkMapSection
import com.mdavila_2001.gopuppy.ui.theme.GoPuppyTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalkDetailScreen(
    navController: NavController,
    walkId: Int,
    isWalker: Boolean = false,
    viewModel: WalkDetailViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val file = FileUtils.getFileFromUri(context, uri)
            if (file != null) {
                viewModel.uploadPhoto(file)
            } else {
                Toast.makeText(context, "Error al procesar imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Cargar detalles del paseo
    LaunchedEffect(walkId) {
        viewModel.loadWalkDetails(walkId)
    }

    LaunchedEffect(state.errorMessage, state.successMessage) {
        state.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessages()
        }
        state.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    GoPuppyTheme(role = if (isWalker) "walker" else "owner") {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                DrawerMenu(
                    navController = navController,
                    isWalker = isWalker,
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
                    AppBar(
                        title = "Detalles del Paseo",
                        logOutEnabled = false,
                        backEnabled = true,
                        onLogoutClick = { },
                        onBackClick = { navController.navigateUp() },
                        modifier = Modifier
                    )
                }
            ) { paddingValues ->
                if (state.isLoading && state.walk == null) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (state.walk == null && state.errorMessage != null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error al cargar el paseo", color = MaterialTheme.colorScheme.error)
                    }
                } else if (state.walk != null) {
                    val walk = state.walk!!

                    val lat = walk.address.lat.toDoubleOrNull() ?: -17.7833
                    val lng = walk.address.lng.toDoubleOrNull() ?: -63.1821

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .verticalScroll(rememberScrollState())
                    ) {
                        WalkMapSection(latitude = lat, longitude = lng)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            StatusChip(
                                status = walk.status,
                                modifier = Modifier.align(Alignment.CenterEnd)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        PetInfoSection(
                            petName = walk.pet.name,
                            petPhoto = walk.pet.photoUrl,
                            ownerName = walk.owner.name,
                            notes = walk.notes
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        WalkDetailsSection(
                            date = walk.scheduledAt,
                            duration = walk.durationMinutes
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        if (isWalker) {
                            val status = walk.status.lowercase()

                            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                                when (status) {
                                    "accepted", "pending", "scheduled" -> {
                                        Button(
                                            text = "Iniciar Paseo",
                                            onClick = { viewModel.startWalk() },
                                            isLoading = state.isLoading
                                        )
                                    }
                                    "in_progress", "started", "en curso" -> {
                                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                            Text(
                                                "Paseo en curso",
                                                style = MaterialTheme.typography.labelLarge,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.align(Alignment.CenterHorizontally)
                                            )

                                            OutlinedButton(
                                                text = "Subir Evidencia",
                                                onClick = {
                                                    photoPickerLauncher.launch(
                                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                    )
                                                }
                                            )

                                            DangerButton(
                                                text = "Finalizar Paseo",
                                                onClick = {
                                                    viewModel.endWalk()
                                                },
                                                isLoading = state.isLoading
                                            )
                                        }
                                    }
                                    "finished" -> {
                                        Text(
                                            text = "✅ Este paseo ha finalizado",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = Color.Gray,
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WalkDetailScreenPreview() {
    GoPuppyTheme(role = "walker") {
        WalkDetailScreen(
            navController = rememberNavController(),
            walkId = 1,
            isWalker = true
        )
    }
}
