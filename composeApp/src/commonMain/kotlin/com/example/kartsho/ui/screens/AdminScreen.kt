package com.example.kartsho.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kartsho.ui.state.KartshoState

@Composable
fun AdminScreen(
    state: KartshoState,
    onApproveProduct: (String) -> Unit,
    onRejectProduct: (String) -> Unit
) {
    val pending = state.products.filter { !it.approved }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            OverviewStrip(
                title = "Admin Review",
                subtitle = "Validate and approve supplier listings to make them live on the marketplace.",
                stats = listOf(
                    "Pending" to pending.size.toString(),
                    "Total Users" to state.users.size.toString()
                )
            )
        }

        if (pending.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)
                ) {
                    Text(
                        "✅ All clear! No pending items requiring review.",
                        modifier = Modifier.padding(20.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        items(pending) { product ->
            Column(modifier = Modifier.padding(16.dp)) {
                ProductCard(product = product, compact = true)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onRejectProduct(product.id) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Reject", color = MaterialTheme.colorScheme.error)
                    }
                    Button(
                        onClick = { onApproveProduct(product.id) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Approve Listing")
                    }
                }
            }
        }
        
        item {
            Text(
                "System Users",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
        
        items(state.users) { user ->
            Surface(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                tonalElevation = 1.dp
            ) {
                Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(user.name, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        Text(user.email, style = MaterialTheme.typography.bodySmall)
                    }
                    Text(user.role.label, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
