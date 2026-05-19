package com.example.kartsho.data.mapper

import com.example.kartsho.domain.model.*
import com.example.kartsho.`data`.local.UserEntity
import com.example.kartsho.`data`.local.ProductEntity
import com.example.kartsho.`data`.local.AuctionEntity

fun UserEntity.toDomain() = User(
    id = id,
    name = name,
    email = email,
    password = passwordHash,
    role = UserRole.entries.firstOrNull { it.label.equals(role, ignoreCase = true) } ?: UserRole.Buyer
)

fun User.toEntity() = UserEntity(
    id = id,
    name = name,
    email = email,
    role = role.label,
    passwordHash = password
)

fun ProductEntity.toDomain() = Product(
    id = id,
    title = title,
    description = description,
    price = price,
    supplierName = supplierName,
    supplierId = supplierId,
    approved = approved,
    createdAtMillis = createdAtMillis,
    colorSeed = colorSeed,
    imageUrl = imageUrl
)

fun Product.toEntity() = ProductEntity(
    id = id,
    title = title,
    description = description,
    price = price,
    supplierName = supplierName,
    supplierId = supplierId,
    approved = approved,
    createdAtMillis = createdAtMillis,
    colorSeed = colorSeed,
    imageUrl = imageUrl
)

fun AuctionEntity.toDomain(): Auction {
    val bids = if (bidHistory.isBlank()) emptyList() else bidHistory.split(";;").mapNotNull { chunk ->
        val parts = chunk.split("::")
        if (parts.size == 3) {
            BidEntry(parts[0], parts[1].toDoubleOrNull() ?: 0.0, parts[2].toLongOrNull() ?: 0L)
        } else null
    }
    return Auction(
        id = id,
        title = title,
        description = description,
        startingPrice = startingPrice,
        currentBid = currentBid,
        currentBidderName = currentBidderName,
        endAtMillis = endAtMillis,
        supplierName = supplierName,
        approved = approved,
        bidHistory = bids,
        colorSeed = colorSeed,
        imageUrl = imageUrl
    )
}

fun Auction.toEntity(): AuctionEntity {
    val bidsStr = bidHistory.joinToString(";;") { "${it.bidderName}::${it.amount}::${it.placedAtMillis}" }
    return AuctionEntity(
        id = id,
        title = title,
        description = description,
        startingPrice = startingPrice,
        currentBid = currentBid,
        currentBidderName = currentBidderName,
        endAtMillis = endAtMillis,
        supplierName = supplierName,
        approved = approved,
        bidHistory = bidsStr,
        colorSeed = colorSeed,
        imageUrl = imageUrl
    )
}
