package com.example.kartsho.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kartsho.ui.state.KartshoState
import com.example.kartsho.util.LocalImagePicker

@Composable
fun SupplierScreen(
    state: KartshoState,
    onAddProduct: (String, String, String, Int, String) -> String?
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    val imagePicker = LocalImagePicker.current.rememberLauncher { uri ->
        if (uri != null) {
            imageUrl = uri
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        OverviewStrip(
            title = "Supplier Portal",
            subtitle = "Manage your catalog. New listings require admin approval before going live.",
            stats = listOf(
                "Your Items" to state.products.count { it.supplierId == state.session?.id }.toString(),
                "Pending" to state.products.count { it.supplierId == state.session?.id && !it.approved }.toString()
            )
        )

        Surface(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Add New Listing", style = MaterialTheme.typography.titleLarge)
                
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Product Title") }
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Description") },
                    minLines = 3
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Price (₹)") }
                )

                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Image URL (Or click Media upload)") }
                )

                if (imageUrl.isNotBlank()) {
                    DemoIllustrationBanner(
                        seed = 0,
                        height = 200.dp,
                        badgeText = "Preview",
                        title = title.ifBlank { "Preview" },
                        imageUrl = imageUrl
                    )
                }

                Button(
                    onClick = {
                        imagePicker()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Upload from Media")
                }

                Button(
                    onClick = {
                        error = onAddProduct(title, description, price, (0..3).random(), imageUrl)
                        if (error == null) {
                            title = ""
                            description = ""
                            price = ""
                            imageUrl = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Submit for Review")
                }

                error?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            }
        }

        Text(
            "Your Recent Listings",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )

        state.products.filter { it.supplierId == state.session?.id }.forEach { product ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                ProductCard(product = product, compact = true)
            }
        }
    }
}
