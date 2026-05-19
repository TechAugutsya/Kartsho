package com.example.kartsho.ui.state

import com.example.kartsho.domain.model.Auction
import com.example.kartsho.domain.model.Product
import com.example.kartsho.domain.model.User

enum class AppSection(val label: String) {
    Shop("Shop"),
    Auctions("Auctions"),
    Upload("Upload"),
    Review("Review")
}

data class KartshoState(
    val session: User? = null,
    val users: List<User> = emptyList(),
    val products: List<Product> = emptyList(),
    val auctions: List<Auction> = emptyList(),
    val section: AppSection = AppSection.Shop,
    val banner: String? = null,
    val loading: Boolean = false,
    val selectedProductId: String? = null,
    val cart: List<Product> = emptyList(),
    val showCartDialog: Boolean = false,
    val checkoutProduct: Product? = null,
    val checkoutCart: Boolean = false
)
