package com.mdavila_2001.gopuppy.ui.views.register

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mdavila_2001.gopuppy.R
import com.mdavila_2001.gopuppy.ui.NavRoutes
import com.mdavila_2001.gopuppy.ui.components.global.buttons.Button
import com.mdavila_2001.gopuppy.ui.components.global.inputs.Input
import com.mdavila_2001.gopuppy.ui.components.global.inputs.PasswordInput
import com.mdavila_2001.gopuppy.ui.theme.GoPuppyTheme

@Composable
fun RegisterScreen(
    navController: NavController,
    isWalker: Boolean = false
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var pricePerHour by remember { mutableStateOf("") }

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
                text = "Crear Cuenta",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo de nombre completo
            Input(
                text = name,
                onValueChange = { name = it },
                label = "Nombre Completo",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de correo electrónico
            Input(
                text = email,
                onValueChange = { email = it },
                label = "Correo Electrónico",
                keyboardType = KeyboardType.Email,
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

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de confirmar contraseña
            PasswordInput(
                password = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirmar Contraseña",
                modifier = Modifier.fillMaxWidth()
            )

            // Campo de precio por hora (solo para paseadores)
            if (isWalker) {
                Spacer(modifier = Modifier.height(16.dp))

                Input(
                    text = pricePerHour,
                    onValueChange = { pricePerHour = it },
                    label = "Precio por hora",
                    keyboardType = KeyboardType.Text,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Registrarse
            Button(
                text = if (isWalker) "Registrarse (Paseador)" else "Registrarse (Dueño)",
                onClick = {
                    // TODO: Implementar lógica de registro con ViewModel
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

            // ¿Ya tienes una cuenta? Inicia sesión
            Text(
                text = "¿Ya tienes una cuenta?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = {
                    navController.popBackStack()
                }
            ) {
                Text(
                    text = "Inicia sesión",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    GoPuppyTheme {
        RegisterScreen(navController = rememberNavController(), isWalker = false)
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenWalkerPreview() {
    GoPuppyTheme {
        RegisterScreen(navController = rememberNavController(), isWalker = true)
    }
}
