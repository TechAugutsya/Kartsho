package com.example.kartsho

import androidx.compose.ui.window.ComposeUIViewController
import com.example.kartsho.di.AppModule
import com.example.kartsho.ui.theme.KartshoTheme
import com.example.kartsho.ui.viewmodel.KartshoViewModel

@Suppress("FunctionName")
fun MainViewController() = ComposeUIViewController {
    val viewModel = KartshoViewModel(AppModule.repository)
    KartshoTheme {
        KartshoApp(viewModel = viewModel)
    }
}
