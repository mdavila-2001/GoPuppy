package com.mdavila_2001.gopuppy.ui.components.global

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mdavila_2001.gopuppy.ui.theme.GoPuppyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    title: String,
    logOutEnabled: Boolean = true,
    backEnabled: Boolean = false,
    onLogoutClick: () -> Unit,
    onBackClick: (() -> Unit)? = null,
    modifier: Modifier
) {

    TopAppBar(
        title = {
            Box(
                modifier = modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onPrimary
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
                        tint = MaterialTheme.colorScheme.onPrimary
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
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Preview(showBackground = true)
@Composable
fun WalkerLightAppBarPreview() {
    GoPuppyTheme(role = "walker", darkTheme = false) {
        AppBar(
            title = "GoPuppy",
            logOutEnabled = true,
            backEnabled = true,
            onLogoutClick = {},
            onBackClick = {},
            modifier = Modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WalkerDarkAppBarPreview() {
    GoPuppyTheme(role = "walker", darkTheme = true) {
        AppBar(
            title = "GoPuppy",
            logOutEnabled = true,
            backEnabled = true,
            onLogoutClick = {},
            onBackClick = {},
            modifier = Modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OwnerLightAppBarPreview() {
    GoPuppyTheme(role = "owner", darkTheme = false) {
        AppBar(
            title = "GoPuppy",
            logOutEnabled = true,
            backEnabled = true,
            onLogoutClick = {},
            onBackClick = {},
            modifier = Modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OwnerDarkAppBarPreview() {
    GoPuppyTheme(role = "owner", darkTheme = true) {
        AppBar(
            title = "GoPuppy",
            logOutEnabled = true,
            backEnabled = true,
            onLogoutClick = {},
            onBackClick = {},
            modifier = Modifier
        )
    }
}