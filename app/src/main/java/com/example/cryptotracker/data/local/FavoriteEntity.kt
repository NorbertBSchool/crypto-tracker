package com.example.cryptotracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val pairAddress: String,
    val chainId: String,
    val dexId: String,
    val baseTokenSymbol: String,
    val baseTokenName: String,
    val quoteTokenSymbol: String,
    val priceUsd: String?,
    val addedAt: Long = System.currentTimeMillis()
)
