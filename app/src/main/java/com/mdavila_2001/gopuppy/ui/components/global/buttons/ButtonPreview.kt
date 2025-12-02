package com.mdavila_2001.gopuppy.ui.components.global.buttons

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mdavila_2001.gopuppy.ui.theme.GoPuppyTheme

@Preview(showBackground = true)
@Composable
fun OwnerButtonPreview() {
    GoPuppyTheme(role = "owner") {
        Column(modifier = Modifier.padding(16.dp)) {
            Button(text = "Iniciar Sesión (Dueño)", onClick = {})
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(text = "Registrarse (Dueño)", onClick = {})
            Spacer(modifier = Modifier.height(8.dp))
            DangerButton(text = "Eliminar Cuenta (Dueño)", onClick = {})
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WalkerButtonPreview() {
    GoPuppyTheme(role = "walker") {
        Column(modifier = Modifier.padding(16.dp)) {
            Button(text = "Iniciar Sesión (Paseador)", onClick = {})
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(text = "Registrarse (Paseador)", onClick = {})
            Spacer(modifier = Modifier.height(8.dp))
            DangerButton(text = "Eliminar Cuenta (Paseador)", onClick = {})
        }
    }
}