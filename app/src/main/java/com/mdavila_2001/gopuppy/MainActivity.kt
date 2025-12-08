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
import com.mdavila_2001.gopuppy.ui.components.global.NavigationApp
import com.mdavila_2001.gopuppy.ui.theme.GoPuppyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            android.util.Log.e("CRASH", "════════════════════════════════════════")
            android.util.Log.e("CRASH", "App crashed on thread: ${thread.name}")
            android.util.Log.e("CRASH", "Exception: ${throwable.message}")
            android.util.Log.e("CRASH", "Stack trace:", throwable)
            android.util.Log.e("CRASH", "════════════════════════════════════════")
            throw throwable
        }
        RetrofitInstance.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            GoPuppyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavigationApp(modifier = Modifier.padding(innerPadding))
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