package com.mdavila_2001.gopuppy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mdavila_2001.gopuppy.ui.theme.GoPuppyTheme
import com.mdavila_2001.gopuppy.ui.theme.OwnerPrimary
import com.mdavila_2001.gopuppy.ui.theme.OwnerSecondary
import com.mdavila_2001.gopuppy.ui.theme.WalkerPrimary
import com.mdavila_2001.gopuppy.ui.theme.WalkerSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    title: String,
    logOutEnabled: Boolean = true,
    backEnabled: Boolean = false,
    onLogoutClick: () -> Unit,
    onBackClick: (() -> Unit)? = null,
    role: String,
    modifier: Modifier
) {
    val (primaryColor, secondaryColor) = if (role.lowercase() == "walker") {
        Pair(WalkerPrimary, WalkerSecondary)
    } else {
        Pair(OwnerPrimary, OwnerSecondary)
    }

    TopAppBar(
        title = {
            Box(
                modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    title,
                    color = Color.Black
                )
            }
        },
        navigationIcon = {
            if (backEnabled && onBackClick != null) {
                IconButton(
                    onClick = onBackClick
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.Black
                    )
                }
            }
        },
        actions = {
            if (logOutEnabled) {
                IconButton(
                    onClick = onLogoutClick
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Salir",
                        tint = Color.Black
                    )
                }
            }
        },
        modifier = modifier
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        primaryColor,
                        secondaryColor
                    ),
                    start = Offset(-200f, 0f),
                    end = Offset(500f, 1000f)
                )
            ),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Preview(showBackground = true)
@Composable
fun AppBarPreview() {
    GoPuppyTheme() {
        AppBar(
            title = "GoPuppy",
            logOutEnabled = true,
            backEnabled = true,
            onLogoutClick = {},
            onBackClick = {},
            role = "Walker",
            modifier = Modifier
        )
    }
}