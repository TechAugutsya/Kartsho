package com.example.kartsho.domain.model

data class Product(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val supplierName: String,
    val supplierId: String,
    val approved: Boolean,
    val createdAtMillis: Long,
    val colorSeed: Int,
    val imageUrl: String? = null
)
