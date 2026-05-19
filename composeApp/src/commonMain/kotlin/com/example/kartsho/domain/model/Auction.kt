package com.example.kartsho.domain.model

data class BidEntry(
    val bidderName: String,
    val amount: Double,
    val placedAtMillis: Long
)

data class Auction(
    val id: String,
    val title: String,
    val description: String,
    val startingPrice: Double,
    val currentBid: Double,
    val currentBidderName: String?,
    val endAtMillis: Long,
    val supplierName: String,
    val approved: Boolean,
    val bidHistory: List<BidEntry>,
    val colorSeed: Int,
    val imageUrl: String? = null
)
