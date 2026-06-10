package com.example.cryptotracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "holdings")
data class HoldingEntity(
    @PrimaryKey
    val pairAddress: String,
    val chainId: String,
    val dexId: String,
    val tokenSymbol: String,
    val tokenName: String,
    val quoteTokenSymbol: String,
    val buyPriceUsd: Double,
    val quantity: Double,
    val addedAt: Long = System.currentTimeMillis()
)
