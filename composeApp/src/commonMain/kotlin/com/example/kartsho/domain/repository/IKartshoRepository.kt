package com.example.kartsho.domain.repository

import com.example.kartsho.domain.model.Auction
import com.example.kartsho.domain.model.AuthMode
import com.example.kartsho.domain.model.User
import com.example.kartsho.domain.model.Product
import com.example.kartsho.domain.model.UserRole

interface IKartshoRepository {
    suspend fun checkActiveSession(): User?
    fun logoutSession()
    suspend fun seedIfNeeded()
    suspend fun getUsers(): List<User>
    suspend fun getProducts(): List<Product>
    suspend fun getAuctions(): List<Auction>
    suspend fun authenticate(
        mode: AuthMode,
        role: UserRole,
        name: String,
        email: String,
        password: String
    ): Pair<User?, String?>

    suspend fun addProduct(
        session: User,
        title: String,
        description: String,
        priceText: String,
        colorSeed: Int,
        imageUrl: String
    ): Pair<Product?, String?>

    suspend fun approveProduct(productId: String)
    suspend fun rejectProduct(productId: String)
    suspend fun placeBid(
        session: User,
        auctionId: String,
        bidText: String
    ): Pair<Auction?, String?>
}
