package com.example.cryptotracker.domain.model

data class PortfolioItem(
    val pairAddress: String,
    val chainId: String,
    val dexId: String,
    val tokenSymbol: String,
    val tokenName: String,
    val quoteTokenSymbol: String,
    val buyPriceUsd: Double,
    val quantity: Double,
    val currentPriceUsd: Double,
    val imageUrl: String?
) {
    val totalCost: Double get() = buyPriceUsd * quantity
    val currentValue: Double get() = currentPriceUsd * quantity
    val pnlUsd: Double get() = currentValue - totalCost
    val pnlPercent: Double get() = if (buyPriceUsd > 0) ((currentPriceUsd - buyPriceUsd) / buyPriceUsd) * 100.0 else 0.0
}
