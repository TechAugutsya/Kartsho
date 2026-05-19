package com.example.kartsho.`data`.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "",
    val passwordHash: String = ""
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String = "",
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val supplierName: String = "",
    val supplierId: String = "",
    val approved: Boolean = false,
    val createdAtMillis: Long = 0L,
    val colorSeed: Int = 0,
    val imageUrl: String? = null
)

@Entity(tableName = "auctions")
data class AuctionEntity(
    @PrimaryKey val id: String = "",
    val title: String = "",
    val description: String = "",
    val startingPrice: Double = 0.0,
    val currentBid: Double = 0.0,
    val currentBidderName: String? = null,
    val endAtMillis: Long = 0L,
    val supplierName: String = "",
    val approved: Boolean = false,
    val bidHistory: String = "",
    val colorSeed: Int = 0,
    val imageUrl: String? = null
)
