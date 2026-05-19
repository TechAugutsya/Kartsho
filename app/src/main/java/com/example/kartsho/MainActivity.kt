package com.example.kartsho

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import com.example.kartsho.di.AppModule
import com.example.kartsho.ui.theme.KartshoTheme
import com.example.kartsho.ui.viewmodel.KartshoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize global context for shared module
        appContext = applicationContext
        
        SingletonImageLoader.setSafe { context ->
            ImageLoader.Builder(context)
                .components {
                    add(KtorNetworkFetcherFactory())
                }
                .crossfade(true)
                .build()
        }
        
        val viewModel = KartshoViewModel(AppModule.repository)

        setContent {
            KartshoTheme {
                KartshoApp(viewModel = viewModel)
            }
        }
    }
}
