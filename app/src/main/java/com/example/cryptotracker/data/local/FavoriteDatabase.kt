package com.example.cryptotracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [FavoriteEntity::class, HoldingEntity::class],
    version = 2,
    exportSchema = false
)
abstract class FavoriteDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun holdingDao(): HoldingDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE IF NOT EXISTS holdings (
                pairAddress TEXT NOT NULL PRIMARY KEY,
                chainId TEXT NOT NULL,
                dexId TEXT NOT NULL,
                tokenSymbol TEXT NOT NULL,
                tokenName TEXT NOT NULL,
                quoteTokenSymbol TEXT NOT NULL,
                buyPriceUsd REAL NOT NULL,
                quantity REAL NOT NULL,
                addedAt INTEGER NOT NULL
            )"""
        )
    }
}
