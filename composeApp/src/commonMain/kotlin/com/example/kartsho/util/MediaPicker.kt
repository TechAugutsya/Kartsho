package com.example.kartsho.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

interface ImagePicker {
    @Composable
    fun rememberLauncher(onResult: (String?) -> Unit): () -> Unit
}

val LocalImagePicker = staticCompositionLocalOf<ImagePicker> {
    error("No ImagePicker provided")
}
