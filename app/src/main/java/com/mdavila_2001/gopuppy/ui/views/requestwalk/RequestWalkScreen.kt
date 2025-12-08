package com.mdavila_2001.gopuppy.ui.views.requestwalk

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.view.MotionEvent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.mdavila_2001.gopuppy.data.remote.models.address.Address
import com.mdavila_2001.gopuppy.data.remote.models.pet.Pet
import com.mdavila_2001.gopuppy.ui.components.global.buttons.Button
import com.mdavila_2001.gopuppy.ui.components.global.cards.WalkerCard
import com.mdavila_2001.gopuppy.ui.components.global.inputs.Input
import com.mdavila_2001.gopuppy.ui.theme.GoPuppyTheme
import com.mdavila_2001.gopuppy.ui.viewmodels.walk.RequestWalkViewModel
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun RequestWalkScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddPet: () -> Unit,
    viewModel: RequestWalkViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val view = LocalView.current
    val scrollState = rememberScrollState()

    var dateText by remember { mutableStateOf("") }
    var timeText by remember { mutableStateOf("") }
    var scheduledAt by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("30") }
    var notes by remember { mutableStateOf("") }

    val defaultLocation = LatLng(-17.7833, -63.1821)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 14f)
    }

    LaunchedEffect(Unit) {
        viewModel.loadInitialData()
    }

    LaunchedEffect(state.selectedAddress?.id) {
        state.selectedAddress?.let { address ->
            val lat = address.latitude?.toDoubleOrNull()
            val lng = address.longitude?.toDoubleOrNull()

            if (lat != null && lng != null) {
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newCameraPosition(
                        CameraPosition.fromLatLngZoom(LatLng(lat, lng), 15f)
                    ),
                    durationMs = 800
                )
            }
        }
    }

    LaunchedEffect(state.errorMessage, state.successMessage) {
        state.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessages()
        }
        state.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
            onNavigateBack()
        }
    }

    GoPuppyTheme(role = "owner") {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Solicitar Paseo", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { onNavigateBack() }) {
                            Icon(Icons.Default.ArrowBack, "Volver")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )
            },
            bottomBar = {
                Surface(
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Button(
                            text = "Confirmar Solicitud",
                            isLoading = state.isLoading,
                            enabled = state.selectedWalker != null && state.selectedPet != null && scheduledAt.isNotEmpty(),
                            onClick = {
                                viewModel.submitRequest(
                                    scheduledAt = scheduledAt,
                                    durationMinutes = duration.toIntOrNull() ?: 30,
                                    notes = notes
                                )
                            }
                        )
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
            ) {
                Box(modifier = Modifier.height(350.dp).fillMaxWidth()) {
                    GoogleMap(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInteropFilter { motionEvent ->
                                when (motionEvent.action) {
                                    MotionEvent.ACTION_DOWN -> {
                                        view.parent?.requestDisallowInterceptTouchEvent(true)
                                    }
                                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                                        view.parent?.requestDisallowInterceptTouchEvent(false)
                                    }
                                }
                                false
                            },
                        cameraPositionState = cameraPositionState,
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = true,
                            scrollGesturesEnabled = true,
                            zoomGesturesEnabled = true,
                            rotationGesturesEnabled = false,
                            tiltGesturesEnabled = false
                        )
                    ) {
                        state.selectedAddress?.let { selectedAddr ->
                            val myPos = LatLng(
                                selectedAddr.latitude?.toDoubleOrNull() ?: -17.7833,
                                selectedAddr.longitude?.toDoubleOrNull() ?: -63.1821
                            )
                            Marker(
                                state = rememberUpdatedMarkerState(position = myPos),
                                title = "Punto de Encuentro",
                                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                            )
                        }

                        state.nearbyWalkers.forEach { walker ->
                            val wLat = walker.latitude
                            val wLng = walker.longitude
                            if (wLat != null && wLng != null) {
                                Marker(
                                    state = rememberUpdatedMarkerState(position = LatLng(wLat, wLng)),
                                    title = walker.name,
                                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE),
                                    onClick = {
                                        viewModel.onWalkerSelected(walker)
                                        false
                                    }
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(16.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.95f))
                            .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                    ) {
                        AddressDropdown(
                            addresses = state.myAddresses,
                            selectedAddress = state.selectedAddress,
                            onAddressSelected = { viewModel.onAddressSelected(it) }
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    if (state.selectedWalker != null) {
                        Text("Paseador Seleccionado", fontWeight = FontWeight.Bold)
                        WalkerCard(
                            name = state.selectedWalker!!.name,
                            price = state.selectedWalker!!.priceHour,
                            rating = state.selectedWalker!!.rating ?: 5.0,
                            imageURL = state.selectedWalker!!.photoUrl,
                            onClick = {}
                        )
                    } else {
                        Text(
                            "👆 Toca un pin naranja en el mapa para elegir paseador",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }

                    HorizontalDivider()

                    Text("¿A quién paseamos?", fontWeight = FontWeight.Bold)
                    if (state.myPets.isEmpty()) {
                        Text("No tienes mascotas registradas.", color = Color.Gray)
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(state.myPets) { pet ->
                                PetSelectionCard(
                                    pet = pet,
                                    isSelected = state.selectedPet?.id == pet.id,

                                    onClick = { viewModel.onPetSelected(pet) }
                                )
                            }
                        }
                    }

                    Text("Detalles del Paseo", fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = dateText,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Fecha") },
                            modifier = Modifier.weight(1f),
                            trailingIcon = {
                                IconButton(onClick = {
                                    showDatePicker(context) { y, m, d ->
                                        dateText = "$d/${m+1}/$y"
                                        scheduledAt = String.format(Locale.getDefault(), "%04d-%02d-%02d", y, m + 1, d) + " " + (if(timeText.isEmpty()) "00:00" else timeText)
                                    }
                                }) {
                                    Icon(Icons.Default.CalendarToday, null)
                                }
                            }
                        )
                        OutlinedTextField(
                            value = timeText,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Hora") },
                            modifier = Modifier.weight(1f),
                            trailingIcon = {
                                IconButton(onClick = {
                                    showTimePicker(context) { h, min ->
                                        timeText = String.format(Locale.getDefault(), "%02d:%02d", h, min)
                                        val datePart = if(scheduledAt.contains(" ")) scheduledAt.split(" ")[0] else scheduledAt
                                        scheduledAt = "$datePart $timeText"
                                    }
                                }) {
                                    Icon(Icons.Default.Schedule, null)
                                }
                            }
                        )
                    }

                    Input(
                        text = duration,
                        onValueChange = { duration = it },
                        label = "Duración (minutos)",
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    )

                    Input(
                        text = notes,
                        onValueChange = { notes = it },
                        label = "Notas para el paseador",
                        singleLine = false,
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(60.dp))
                }
            }
        }
    }
}

@Composable
fun AddressDropdown(
    addresses: List<Address>,
    selectedAddress: Address?,
    onAddressSelected: (Address) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("Ubicación de Recojo", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text(
                    text = selectedAddress?.label ?: "Selecciona una dirección",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            addresses.forEach { address ->
                DropdownMenuItem(
                    text = { Text("${address.label} - ${address.address}") },
                    onClick = {
                        onAddressSelected(address)
                        expanded = false
                    }
                )
            }
            if (addresses.isEmpty()) {
                DropdownMenuItem(text = { Text("No hay direcciones guardadas") }, onClick = {})
            }
        }
    }
}

@Composable
fun PetSelectionCard(pet: Pet, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(width = 100.dp, height = 130.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else BorderStroke(1.dp, Color.LightGray)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (!pet.photoUrl.isNullOrBlank()) {
                    coil3.compose.AsyncImage(
                        model = pet.photoUrl,
                        contentDescription = "Foto de ${pet.name}",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.Pets,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                pet.name,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                style = MaterialTheme.typography.bodySmall
            )
            if (pet.type != null) {
                Text(
                    pet.type,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    maxLines = 1
                )
            }
        }
    }
}

fun showDatePicker(context: android.content.Context, onDateSelected: (Int, Int, Int) -> Unit) {
    val calendar = Calendar.getInstance()
    DatePickerDialog(
        context,
        { _, year, month, day -> onDateSelected(year, month, day) },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}

fun showTimePicker(context: android.content.Context, onTimeSelected: (Int, Int) -> Unit) {
    val calendar = Calendar.getInstance()
    TimePickerDialog(
        context,
        { _, hour, minute -> onTimeSelected(hour, minute) },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    ).show()
}