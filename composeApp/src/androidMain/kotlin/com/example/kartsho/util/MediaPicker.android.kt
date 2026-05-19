package com.example.kartsho.util

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.io.File
import java.util.UUID

class AndroidImagePicker : ImagePicker {
    @Composable
    override fun rememberLauncher(onResult: (String?) -> Unit): () -> Unit {
        val context = LocalContext.current
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri: Uri? ->
            if (uri != null) {
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val cacheFile = File(context.cacheDir, "img_${UUID.randomUUID()}.jpg")
                    cacheFile.outputStream().use { output ->
                        inputStream?.copyTo(output)
                    }
                    inputStream?.close()
                    onResult("file://${cacheFile.absolutePath}")
                } catch (e: Exception) {
                    e.printStackTrace()
                    onResult(null)
                }
            } else {
                onResult(null)
            }
        }

        return remember {
            {
                launcher.launch(
                    androidx.activity.result.PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }
        }
    }
}
