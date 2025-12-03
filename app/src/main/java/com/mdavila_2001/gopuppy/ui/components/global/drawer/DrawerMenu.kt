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
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
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
        // Header
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
        
        // Menu Items
        DrawerMenuItem(
            icon = Icons.Default.Home,
            title = "Inicio",
            onClick = {
                onCloseDrawer()
            }
        )
        
        DrawerMenuItem(
            icon = Icons.Default.Person,
            title = "Mi Perfil",
            onClick = {
                // TODO: Navegar a perfil
                onCloseDrawer()
            }
        )
        
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
                // TODO: Implementar logout
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
