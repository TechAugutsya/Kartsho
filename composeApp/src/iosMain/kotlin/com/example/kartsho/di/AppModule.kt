package com.example.kartsho.di

import androidx.room.Room
import com.example.kartsho.IosSettings
import com.example.kartsho.data.local.KartshoDatabase
import com.example.kartsho.data.local.KartshoDatabaseConstructor
import com.example.kartsho.data.local.getRoomDatabase
import com.example.kartsho.data.repository.KartshoRepositoryImpl
import com.example.kartsho.domain.repository.IKartshoRepository
import com.example.kartsho.util.ImagePicker
import com.example.kartsho.util.IosImagePicker
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import platform.Foundation.NSHomeDirectory

actual object AppModule {
    private val database: KartshoDatabase by lazy {
        val dbFilePath = NSHomeDirectory() + "/Documents/zeerostock_db"
        val builder = Room.databaseBuilder<KartshoDatabase>(
            name = dbFilePath,
            factory = KartshoDatabaseConstructor::initialize
        )
        getRoomDatabase(builder)
    }

    actual val repository: IKartshoRepository by lazy {
        KartshoRepositoryImpl(
            dao = database.zeerostockDao(),
            auth = Firebase.auth,
            firestore = Firebase.firestore,
            settings = IosSettings()
        )
    }

    actual val imagePicker: ImagePicker by lazy { IosImagePicker() }
}
