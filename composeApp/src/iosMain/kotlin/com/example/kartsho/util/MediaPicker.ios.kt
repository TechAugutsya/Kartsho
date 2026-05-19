package com.example.kartsho.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberImagePickerLauncher(onResult: (String?) -> Unit): () -> Unit {
    return remember {
        {
            // For a complete iOS implementation, UIImagePickerController needs to be wrapped via UIViewControllerRepresentable.
            // As a quick fallback for KMP prototypes, we return a sample image if native media picking isn't fully set up yet.
            onResult("https://images.unsplash.com/photo-1542291026-7eec264c27ff?q=80&w=1000&auto=format&fit=crop")
        }
    }
}
