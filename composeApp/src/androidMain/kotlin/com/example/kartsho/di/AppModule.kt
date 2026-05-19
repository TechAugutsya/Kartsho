package com.example.kartsho.di

import androidx.room.Room
import com.example.kartsho.AndroidSettings
import com.example.kartsho.appContext
import com.example.kartsho.data.local.KartshoDatabase
import com.example.kartsho.data.local.KartshoDatabaseConstructor
import com.example.kartsho.data.local.getRoomDatabase
import com.example.kartsho.data.repository.KartshoRepositoryImpl
import com.example.kartsho.domain.repository.IKartshoRepository
import com.example.kartsho.util.AndroidImagePicker
import com.example.kartsho.util.ImagePicker
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore

actual object AppModule {
    private val database: KartshoDatabase by lazy {
        val builder = Room.databaseBuilder<KartshoDatabase>(
            context = appContext.applicationContext,
            name = appContext.getDatabasePath("zeerostock_db").absolutePath,
            factory = KartshoDatabaseConstructor::initialize
        )
        getRoomDatabase(builder)
    }

    actual val repository: IKartshoRepository by lazy {
        KartshoRepositoryImpl(
            dao = database.zeerostockDao(),
            auth = Firebase.auth,
            firestore = Firebase.firestore,
            settings = AndroidSettings(appContext)
        )
    }

    actual val imagePicker: ImagePicker by lazy { AndroidImagePicker() }
}
