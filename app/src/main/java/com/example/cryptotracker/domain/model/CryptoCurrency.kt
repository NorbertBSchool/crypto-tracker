package com.example.cryptotracker.domain.model

data class CryptoCurrency(
    val pairAddress: String,
    val chainId: String,
    val dexId: String,
    val baseTokenSymbol: String,
    val baseTokenName: String,
    val baseTokenAddress: String,
    val quoteTokenSymbol: String,
    val priceUsd: String,
    val priceNative: String,
    val priceChange24h: Double?,
    val priceChange1h: Double?,
    val volume24h: Double?,
    val liquidityUsd: Double?,
    val fdv: Double?,
    val marketCap: Double?,
    val url: String?,
    val imageUrl: String?
)
