package com.example.cryptotracker.domain.model

data class TokenBoostItem(
    val chainId: String,
    val tokenAddress: String,
    val name: String?,
    val bannerUrl: String?
)
