package com.example.kartsho.`data`.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(
    entities = [UserEntity::class, ProductEntity::class, AuctionEntity::class],
    version = 3,
    exportSchema = false,
)
@ConstructedBy(KartshoDatabaseConstructor::class)
abstract class KartshoDatabase : RoomDatabase() {
    abstract fun zeerostockDao(): KartshoDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object KartshoDatabaseConstructor : RoomDatabaseConstructor<KartshoDatabase>

fun getRoomDatabase(
    builder: RoomDatabase.Builder<KartshoDatabase>
): KartshoDatabase {
    return builder
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()
}
