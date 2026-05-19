package com.example.kartsho.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kartsho.domain.model.Product
import com.example.kartsho.ui.state.KartshoState

import androidx.compose.animation.animateContentSize
import androidx.compose.material3.IconButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun MarketplaceScreen(
    state: KartshoState,
    onSelectProduct: (String) -> Unit,
    onAddToCart: (Product) -> Unit,
    onBuyNow: (Product) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val visibleProducts = remember(state.products, searchQuery, selectedCategory) {
        state.products.filter { product ->
            if (!product.approved) return@filter false

            val matchesSearch = searchQuery.isBlank() ||
                product.title.contains(searchQuery, ignoreCase = true) ||
                product.description.contains(searchQuery, ignoreCase = true) ||
                product.supplierName.contains(searchQuery, ignoreCase = true)
            
            val matchesCat = when (selectedCategory) {
                "⚡ Deals" -> product.price < 3000
                "🏷️ Popular" -> true
                "⭐ Top" -> product.price >= 3000
                else -> true
            }

            matchesSearch && matchesCat
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            OverviewStrip(
                title = "Marketplace",
                subtitle = "Explore verified items directly from suppliers. Tap any item to inspect stock.",
                stats = listOf(
                    "Items" to visibleProducts.size.toString(),
                    "Suppliers" to visibleProducts.map { it.supplierName }.distinct().count().toString()
                )
            )
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .animateContentSize()
            ) {
                androidx.compose.material3.OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("🔍 Search products, brands...") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Text("❌")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("All", "⚡ Deals", "🏷️ Popular", "⭐ Top").forEach { cat ->
                        androidx.compose.material3.FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) }
                        )
                    }
                }
            }
        }

        if (visibleProducts.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = "🔍 No products matched your search or category filter.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            item {
                Spacer(modifier = Modifier.height(4.dp))
            }
            items(visibleProducts, key = { it.id }) { product ->
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    ProductCard(
                        product = product,
                        onClick = { onSelectProduct(product.id) },
                        onAddToCart = { onAddToCart(product) },
                        onBuyNow = { onBuyNow(product) }
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    compact: Boolean = false,
    onClick: (() -> Unit)? = null,
    onAddToCart: (() -> Unit)? = null,
    onBuyNow: (() -> Unit)? = null
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            DemoIllustrationBanner(
                seed = product.colorSeed,
                height = if (compact) 180.dp else 240.dp,
                badgeText = "📦 Verified Stock",
                title = product.title,
                imageUrl = product.imageUrl
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(product.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                ApprovalChip(approved = product.approved)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = product.description,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = if (compact) 2 else Int.MAX_VALUE
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text(product.supplierName) })
                AssistChip(onClick = {}, label = { Text("₹${product.price.toInt()}") })
                AssistChip(onClick = {}, label = { Text("⭐ 4.8") })
            }
            if (onAddToCart != null && onBuyNow != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    androidx.compose.material3.OutlinedButton(
                        onClick = onAddToCart,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("🛒 Add to Cart")
                    }
                    androidx.compose.material3.Button(
                        onClick = onBuyNow,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("⚡ Buy Now")
                    }
                }
            }
        }
    }
}
