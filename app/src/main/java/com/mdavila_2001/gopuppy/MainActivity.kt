package com.mdavila_2001.gopuppy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mdavila_2001.gopuppy.data.remote.network.RetrofitInstance
import com.mdavila_2001.gopuppy.smokeTests.MapTestScreen
import com.mdavila_2001.gopuppy.ui.components.global.NavigationApp
import com.mdavila_2001.gopuppy.ui.theme.GoPuppyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Handler global de excepciones para debugging
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            android.util.Log.e("CRASH", "════════════════════════════════════════")
            android.util.Log.e("CRASH", "App crashed on thread: ${thread.name}")
            android.util.Log.e("CRASH", "Exception: ${throwable.message}")
            android.util.Log.e("CRASH", "Stack trace:", throwable)
            android.util.Log.e("CRASH", "════════════════════════════════════════")
            
            // Re-throw para que el sistema maneje el crash normalmente
            throw throwable
        }
        
        // Inicializar RetrofitInstance con el contexto de la app
        RetrofitInstance.init(applicationContext)
        
        enableEdgeToEdge()
        setContent {
            GoPuppyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    /*Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )*/
                    NavigationApp(modifier = Modifier.padding(innerPadding))
                    //MapTestScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GoPuppyTheme {
        Greeting("Android")
    }
}