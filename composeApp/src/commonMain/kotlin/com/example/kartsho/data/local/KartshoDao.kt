package com.example.kartsho.`data`.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface KartshoDao {
    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserEntity>

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Query("SELECT * FROM products ORDER BY createdAtMillis DESC")
    suspend fun getAllProducts(): List<ProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Query("UPDATE products SET approved = :approved WHERE id = :productId")
    suspend fun updateProductApproval(productId: String, approved: Boolean)

    @Query("DELETE FROM products WHERE id = :productId")
    suspend fun deleteProduct(productId: String)

    @Query("SELECT * FROM auctions ORDER BY endAtMillis ASC")
    suspend fun getAllAuctions(): List<AuctionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuction(auction: AuctionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuctions(auctions: List<AuctionEntity>)
}
