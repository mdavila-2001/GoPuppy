package com.mdavila_2001.gopuppy.ui.views.requestwalk

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mdavila_2001.gopuppy.ui.views.requestwalk.components.AddPetCard
import com.mdavila_2001.gopuppy.ui.views.requestwalk.components.PetSelectionCard
import com.mdavila_2001.gopuppy.ui.views.requestwalk.components.WalkDropdownField
import com.mdavila_2001.gopuppy.ui.views.requestwalk.components.WalkInputField
import com.mdavila_2001.gopuppy.ui.views.requestwalk.components.WalkTextArea
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestWalkScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddPet: () -> Unit,
    viewModel: RequestWalkViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val pets by viewModel.pets.collectAsState()
    val scrollState = rememberScrollState()

    // Formatters para fecha y hora
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    // Opciones de duración
    val durationOptions = listOf("30 minutos", "45 minutos", "60 minutos", "90 minutos")

    // Estados para Date/Time Pickers
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Snackbar host
    val snackbarHostState = remember { SnackbarHostState() }

    // Mostrar mensajes
    LaunchedEffect(state.errorMessage, state.successMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            viewModel.clearMessages()
        }
        state.successMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            viewModel.clearMessages()
            // Opcional: navegar de vuelta después de éxito
            // kotlinx.coroutines.delay(1500)
            // onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Solicitar Paseo",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            // Botón flotante con degradado
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Button(
                    onClick = { viewModel.submitWalkRequest() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(50.dp),
                    enabled = !state.isLoading && state.selectedPetId != null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            text = "Confirmar Paseo",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(bottom = 80.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Sección: Selecciona tu mascota
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Selecciona tu mascota",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(pets) { pet ->
                        PetSelectionCard(
                            pet = pet,
                            isSelected = state.selectedPetId == pet.id.toString(),
                            onClick = { viewModel.selectPet(pet.id.toString()) }
                        )
                    }

                    item {
                        AddPetCard(onClick = onNavigateToAddPet)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sección: Fecha y Hora
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                WalkInputField(
                    label = "Fecha",
                    value = state.date.format(dateFormatter),
                    onValueChange = {},
                    icon = Icons.Default.CalendarToday,
                    modifier = Modifier.weight(1f),
                    placeholder = "Seleccionar fecha",
                    readOnly = true,
                    onClick = { showDatePicker = true }
                )

                WalkInputField(
                    label = "Hora",
                    value = state.time.format(timeFormatter),
                    onValueChange = {},
                    icon = Icons.Default.Schedule,
                    modifier = Modifier.weight(1f),
                    placeholder = "Seleccionar hora",
                    readOnly = true,
                    onClick = { showTimePicker = true }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sección: Duración
            WalkDropdownField(
                label = "Duración",
                value = "${state.durationMinutes} minutos",
                options = durationOptions,
                onValueChange = { selected ->
                    val minutes = selected.replace(" minutos", "").toIntOrNull() ?: 30
                    viewModel.updateDuration(minutes)
                },
                icon = Icons.Default.Timer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Sección: Instrucciones Especiales
            WalkTextArea(
                label = "Instrucciones Especiales",
                value = state.specialInstructions,
                onValueChange = { viewModel.updateSpecialInstructions(it) },
                placeholder = "Ej: Tiene miedo a los ruidos fuertes, no le des premios...",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Sección: Paseador preferido (Opcional)
            WalkInputField(
                label = "Paseador preferido (Opcional)",
                value = state.preferredWalker,
                onValueChange = { viewModel.updatePreferredWalker(it) },
                icon = Icons.Default.Search,
                placeholder = "Buscar paseador",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Date Picker Dialog
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = state.date.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
            )
            
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = java.time.Instant.ofEpochMilli(millis)
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate()
                            viewModel.updateDate(selectedDate)
                        }
                        showDatePicker = false
                    }) {
                        Text("Aceptar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancelar")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        // Time Picker Dialog
        if (showTimePicker) {
            val timePickerState = rememberTimePickerState(
                initialHour = state.time.hour,
                initialMinute = state.time.minute,
                is24Hour = true
            )
            
            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        val selectedTime = java.time.LocalTime.of(
                            timePickerState.hour,
                            timePickerState.minute
                        )
                        viewModel.updateTime(selectedTime)
                        showTimePicker = false
                    }) {
                        Text("Aceptar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showTimePicker = false }) {
                        Text("Cancelar")
                    }
                },
                text = {
                    TimePicker(state = timePickerState)
                }
            )
        }
    }
}
