package com.example.kartsho.di

import com.example.kartsho.domain.repository.IKartshoRepository

expect object AppModule {
    val repository: IKartshoRepository
}
