package com.mdavila_2001.gopuppy.ui.components.global.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mdavila_2001.gopuppy.data.remote.network.RetrofitInstance
import com.mdavila_2001.gopuppy.ui.NavRoutes

@Composable
fun DrawerMenu(
    navController: NavController,
    isWalker: Boolean = false,
    onCloseDrawer: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Perfil",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = if (isWalker) "Paseador" else "Dueño",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Ver perfil",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Divider(modifier = Modifier.padding(vertical = 16.dp))
        
        DrawerMenuItem(
            icon = Icons.Default.Home,
            title = "Inicio",
            onClick = {
                onCloseDrawer()
            }
        )
        
        if (!isWalker) {
            DrawerMenuItem(
                icon = Icons.Default.DirectionsWalk,
                title = "Solicitar Paseo",
                onClick = {
                    navController.navigate(NavRoutes.RequestWalk.route)
                    onCloseDrawer()
                }
            )
            
            DrawerMenuItem(
                icon = Icons.Default.Search,
                title = "Buscar Paseadores",
                onClick = {
                    navController.navigate(NavRoutes.WalkerSearch.route)
                    onCloseDrawer()
                }
            )
            
            DrawerMenuItem(
                icon = Icons.Default.Pets,
                title = "Registrar Mascota",
                onClick = {
                    navController.navigate(NavRoutes.PetForm.route)
                    onCloseDrawer()
                }
            )
        }
        
        DrawerMenuItem(
            icon = Icons.Default.Person,
            title = "Mi Perfil",
            onClick = {
                if (isWalker) {
                    navController.navigate(NavRoutes.WalkerProfile.route)
                } else {
                    navController.navigate(NavRoutes.OwnerProfile.route)
                }
                onCloseDrawer()
            }
        )
        
        if (!isWalker) {
            DrawerMenuItem(
                icon = Icons.Default.DirectionsWalk,
                title = "Historial de Paseos",
                onClick = {
                    navController.navigate(NavRoutes.WalkHistory.route)
                    onCloseDrawer()
                }
            )
        }
        
        DrawerMenuItem(
            icon = Icons.Default.Settings,
            title = "Configuración",
            onClick = {
                // TODO: Navegar a configuración
                onCloseDrawer()
            }
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        Divider(modifier = Modifier.padding(vertical = 16.dp))
        
        DrawerMenuItem(
            icon = Icons.Default.ExitToApp,
            title = "Cerrar Sesión",
            onClick = {
                // Limpiar el token de autenticación
                RetrofitInstance.authToken = null
                
                // Navegar a la pantalla de Onboarding y limpiar el back stack
                navController.navigate(NavRoutes.Onboarding.route) {
                    popUpTo(0) { inclusive = true }
                }
                onCloseDrawer()
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun DrawerMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
