package com.mdavila_2001.gopuppy.smokeTests

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@SuppressLint("UnrememberedMutableState")
@Composable
fun MapTestScreen(
    modifier: Modifier = Modifier
) {
    // Coordenadas de Santa Cruz de la Sierra (Plaza 24 de Septiembre aprox)
    val santaCruz = LatLng(-17.7833, -63.1821)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(santaCruz, 15f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = MarkerState(position = santaCruz),
            title = "¡Aquí estamos pariente!",
            snippet = "Santa Cruz de la Sierra"
        )
    }
}