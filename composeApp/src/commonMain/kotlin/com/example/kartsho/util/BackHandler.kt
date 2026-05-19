package com.example.kartsho.util

import androidx.compose.runtime.Composable

@Composable
fun KartshoBackHandler(enabled: Boolean = true, onBack: () -> Unit) {
    PlatformBackHandler(enabled, onBack)
}

@Composable
expect fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit)
