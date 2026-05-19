package com.example.kartsho.domain.usecase

import com.example.kartsho.domain.model.Product
import com.example.kartsho.domain.repository.IKartshoRepository

class GetProductsUseCase(private val repository: IKartshoRepository) {
    suspend operator fun invoke(): List<Product> {
        return repository.getProducts()
    }
}
