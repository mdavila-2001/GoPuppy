package com.mdavila_2001.gopuppy.ui.views.walker_search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalkerSearchScreen(
    viewModel: WalkerSearchViewModel,
    navController: NavController
) {
    val state by viewModel.state.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Encontrar Paseador",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            // Barra de búsqueda
            SearchBar(
                searchQuery = state.searchQuery,
                onSearchChange = { viewModel.updateSearchQuery(it) }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Filtros
            FilterChips(
                selectedCalification = state.selectedCalification,
                selectedPrice = state.selectedPrice,
                selectedDistance = state.selectedDistance,
                onCalificationChange = { viewModel.updateCalification(it) },
                onPriceChange = { viewModel.updatePrice(it) },
                onDistanceChange = { viewModel.updateDistance(it) }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Lista de paseadores
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.walkers.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay paseadores disponibles",
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.walkers) { walker ->
                        WalkerCard(
                            walker = walker,
                            onViewProfile = { /* TODO: Navegar a perfil */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    searchQuery: String,
    onSearchChange: (String) -> Unit
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp),
        placeholder = { Text("Buscar por nombre...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar"
            )
        },
        shape = RoundedCornerShape(50.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFE8F5E9),
            unfocusedContainerColor = Color(0xFFE8F5E9),
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChips(
    selectedCalification: String,
    selectedPrice: String,
    selectedDistance: String,
    onCalificationChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onDistanceChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Calificación
        var expandedCal by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expandedCal,
            onExpandedChange = { expandedCal = it }
        ) {
            FilterChip(
                selected = selectedCalification != "Calificación",
                onClick = { expandedCal = true },
                label = { Text(selectedCalification) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandedCal,
                onDismissRequest = { expandedCal = false }
            ) {
                listOf("Calificación", "4+", "4.5+", "5").forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onCalificationChange(option)
                            expandedCal = false
                        }
                    )
                }
            }
        }
        
        // Precio
        var expandedPrice by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expandedPrice,
            onExpandedChange = { expandedPrice = it }
        ) {
            FilterChip(
                selected = selectedPrice != "Precio",
                onClick = { expandedPrice = true },
                label = { Text(selectedPrice) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandedPrice,
                onDismissRequest = { expandedPrice = false }
            ) {
                listOf("Precio", "$10-15", "$15-20", "$20+").forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onPriceChange(option)
                            expandedPrice = false
                        }
                    )
                }
            }
        }
        
        // Distancia
        var expandedDist by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expandedDist,
            onExpandedChange = { expandedDist = it }
        ) {
            FilterChip(
                selected = selectedDistance != "Distancia",
                onClick = { expandedDist = true },
                label = { Text(selectedDistance) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandedDist,
                onDismissRequest = { expandedDist = false }
            ) {
                listOf("Distancia", "< 1 km", "< 5 km", "< 10 km").forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onDistanceChange(option)
                            expandedDist = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun WalkerCard(
    walker: WalkerUiModel,
    onViewProfile: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = walker.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$${walker.pricePerHour}/hr",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onViewProfile,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text("Ver Perfil", color = Color.White)
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Foto del paseador
            AsyncImage(
                model = walker.photoUrl,
                contentDescription = "Foto de ${walker.name}",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
        }
    }
}

// Modelo UI para paseadores
data class WalkerUiModel(
    val id: Int,
    val name: String,
    val pricePerHour: Int,
    val photoUrl: String?
)
