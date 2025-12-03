package com.mdavila_2001.gopuppy.ui.views.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mdavila_2001.gopuppy.R
import com.mdavila_2001.gopuppy.ui.NavRoutes
import com.mdavila_2001.gopuppy.ui.components.global.buttons.Button
import com.mdavila_2001.gopuppy.ui.components.global.buttons.OutlinedButton
import com.mdavila_2001.gopuppy.ui.components.global.inputs.Input
import com.mdavila_2001.gopuppy.ui.components.global.inputs.PasswordInput
import com.mdavila_2001.gopuppy.ui.theme.GoPuppyTheme

@Composable
fun LoginScreen(
    navController: NavController,
    isWalker: Boolean = false
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    GoPuppyTheme(role = if (isWalker) "walker" else "owner") {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo circular
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.gopuppy_logo),
                    contentDescription = "Logo GoPuppy",
                    modifier = Modifier.size(70.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Título
            Text(
                text = "GoPuppy",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Bienvenido de vuelta",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo de correo o usuario
            Input(
                text = email,
                onValueChange = { email = it },
                label = "Correo o usuario",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de contraseña
            PasswordInput(
                password = password,
                onValueChange = { password = it },
                label = "Contraseña",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Olvidé mi contraseña
            TextButton(
                onClick = { /* TODO: Implementar recuperación de contraseña */ },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = "¿Olvidé mi contraseña?",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Iniciar Sesión
            Button(
                text = if (isWalker) "Iniciar Sesión (Paseador)" else "Iniciar Sesión (Dueño)",
                onClick = {
                    // TODO: Implementar lógica de login con ViewModel
                    if (isWalker) {
                        navController.navigate(NavRoutes.WalkerHome.route) {
                            popUpTo(NavRoutes.Landing.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(NavRoutes.OwnerHome.route) {
                            popUpTo(NavRoutes.Landing.route) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ¿No tienes cuenta? Registrarse
            Text(
                text = "¿No tienes cuenta?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                text = if (isWalker) "Registrarse (Paseador)" else "Registrarse (Dueño)",
                onClick = {
                    navController.navigate(NavRoutes.Register.createRoute(isWalker))
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    GoPuppyTheme {
        LoginScreen(navController = rememberNavController(), isWalker = false)
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenWalkerPreview() {
    GoPuppyTheme {
        LoginScreen(navController = rememberNavController(), isWalker = true)
    }
}
