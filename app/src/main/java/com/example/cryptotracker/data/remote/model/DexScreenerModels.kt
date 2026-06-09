package com.example.cryptotracker.data.remote.model

import com.google.gson.annotations.SerializedName

data class DexScreenerResponse(
    @SerializedName("schemaVersion") val schemaVersion: String?,
    @SerializedName("pairs") val pairs: List<PairData>?,
    @SerializedName("pair") val pair: PairData?
)

data class PairData(
    @SerializedName("chainId") val chainId: String,
    @SerializedName("dexId") val dexId: String,
    @SerializedName("url") val url: String?,
    @SerializedName("pairAddress") val pairAddress: String,
    @SerializedName("baseToken") val baseToken: Token,
    @SerializedName("quoteToken") val quoteToken: Token,
    @SerializedName("priceNative") val priceNative: String,
    @SerializedName("priceUsd") val priceUsd: String?,
    @SerializedName("txns") val txns: Map<String, TxnsData>?,
    @SerializedName("volume") val volume: Map<String, Double>?,
    @SerializedName("priceChange") val priceChange: Map<String, Double>?,
    @SerializedName("liquidity") val liquidity: Liquidity?,
    @SerializedName("fdv") val fdv: Double?,
    @SerializedName("marketCap") val marketCap: Double?,
    @SerializedName("pairCreatedAt") val pairCreatedAt: Long?,
    @SerializedName("info") val info: PairInfo?
)

data class Token(
    @SerializedName("address") val address: String,
    @SerializedName("name") val name: String,
    @SerializedName("symbol") val symbol: String
)

data class TxnsData(
    @SerializedName("buys") val buys: Int,
    @SerializedName("sells") val sells: Int
)

data class Liquidity(
    @SerializedName("usd") val usd: Double?,
    @SerializedName("base") val base: Double?,
    @SerializedName("quote") val quote: Double?
)

data class PairInfo(
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("websites") val websites: List<Website>?,
    @SerializedName("socials") val socials: List<Social>?
)

data class Website(
    @SerializedName("url") val url: String
)

data class Social(
    @SerializedName("platform") val platform: String,
    @SerializedName("handle") val handle: String
)
