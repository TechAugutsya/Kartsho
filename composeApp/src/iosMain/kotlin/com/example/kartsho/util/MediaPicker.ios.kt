package com.example.kartsho.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

class IosImagePicker : ImagePicker {
    @Composable
    override fun rememberLauncher(onResult: (String?) -> Unit): () -> Unit {
        return remember {
            {
                onResult("https://images.unsplash.com/photo-1542291026-7eec264c27ff?q=80&w=1000&auto=format&fit=crop")
            }
        }
    }
}
