package com.example.kartsho.di

import com.example.kartsho.domain.repository.IKartshoRepository
import com.example.kartsho.util.ImagePicker

expect object AppModule {
    val repository: IKartshoRepository
    val imagePicker: ImagePicker
}
