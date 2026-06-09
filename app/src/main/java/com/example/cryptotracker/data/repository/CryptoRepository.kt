package com.example.cryptotracker.data.repository

import com.example.cryptotracker.data.local.FavoriteDao
import com.example.cryptotracker.data.local.FavoriteEntity
import com.example.cryptotracker.data.remote.DexScreenerApi
import com.example.cryptotracker.data.remote.model.PairData
import com.example.cryptotracker.domain.model.CryptoCurrency
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CryptoRepository @Inject constructor(
    private val api: DexScreenerApi,
    private val favoriteDao: FavoriteDao
) {

    suspend fun getPairData(chainId: String, pairAddress: String): Result<CryptoCurrency> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getPair(chainId, pairAddress)
                val pair = response.pair
                if (pair != null) {
                    Result.success(pair.toCryptoCurrency())
                } else {
                    Result.failure(Exception("Pair not found"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getTrackedPairs(): List<CryptoCurrency> = coroutineScope {
        val trackedPairs = listOf(
            "ethereum" to "0x2287a9620adcbf6250dc71be9ee9b2d3a1ec85a464fc6f5c06669e8d07b61bba",
            "ethereum" to "0xA43fe16908251ee70EF74718545e4FE6C5cCEc9f", // PEPE
            "ethereum" to "0x5d4F3C6fA16908609BAC31Ff148Bd002AA6b8c83", // LINK

            "solana" to "Ckp1kwZqosaLU1h3zWtuaMBubyWM7LX3cxYezRVin7p2", // TRUMP
            "solana" to "4w2cysotX6czaUGmmWg13hDpY4QEMG2CzeKYEQyK9Ama", // TROLL
            "solana" to "FFcYgSSgWHforA9rXXkA48p8YFoz8TSW85Jpo3CQHDyS", // BUTTCOIN
            "solana" to "EE3zk9Fxp9guair2xeReFxf4TsEXeZFFuWETRna2PkcV", // TOECOIN
            "solana" to "ETMhxtENfkMK85TAcveEbZdBv9htziWzDSddmShRP2wB", // WORLD CUP COIN
            "solana" to "3KFCgJ5R3zshW8hTDbzjSrrKSRYmKvsMfhc4Vo4iddxD", // TTT
        )

        trackedPairs.map { (chain, address) ->
            async { getPairData(chain, address) }
        }.mapNotNull { it.await().getOrNull() }
    }

    suspend fun searchTokens(query: String): Result<List<CryptoCurrency>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.searchPairs(query)
                val pairs = response.pairs
                if (!pairs.isNullOrEmpty()) {
                    Result.success(pairs.map { it.toCryptoCurrency() })
                } else {
                    Result.success(emptyList())
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    fun getFavorites(): Flow<List<FavoriteEntity>> = favoriteDao.getAllFavorites()

    fun isFavorite(pairAddress: String): Flow<Boolean> = favoriteDao.isFavorite(pairAddress)

    suspend fun toggleFavorite(currency: CryptoCurrency) {
        val entity = FavoriteEntity(
            pairAddress = currency.pairAddress,
            chainId = currency.chainId,
            dexId = currency.dexId,
            baseTokenSymbol = currency.baseTokenSymbol,
            baseTokenName = currency.baseTokenName,
            quoteTokenSymbol = currency.quoteTokenSymbol,
            priceUsd = currency.priceUsd
        )
        val isFav = favoriteDao.getFavoriteAddresses().contains(currency.pairAddress)
        if (isFav) {
            favoriteDao.deleteFavorite(currency.pairAddress)
        } else {
            favoriteDao.insertFavorite(entity)
        }
    }

    suspend fun getFavoritePairs(): List<CryptoCurrency> = withContext(Dispatchers.IO) {
        val entities = favoriteDao.getAllFavoritesList()
        if (entities.isEmpty()) return@withContext emptyList()

        coroutineScope {
            entities.map { entity ->
                async {
                    getPairData(entity.chainId, entity.pairAddress).getOrNull()
                }
            }.mapNotNull { it.await() }
        }
    }

    private fun PairData.toCryptoCurrency(): CryptoCurrency {
        return CryptoCurrency(
            pairAddress = pairAddress,
            chainId = chainId,
            dexId = dexId,
            baseTokenSymbol = baseToken.symbol,
            baseTokenName = baseToken.name,
            baseTokenAddress = baseToken.address,
            quoteTokenSymbol = quoteToken.symbol,
            priceUsd = priceUsd ?: "0",
            priceNative = priceNative,
            priceChange24h = priceChange?.get("h24"),
            priceChange1h = priceChange?.get("h1"),
            volume24h = volume?.get("h24"),
            liquidityUsd = liquidity?.usd,
            fdv = fdv,
            marketCap = marketCap,
            url = url,
            imageUrl = info?.imageUrl
        )
    }
}
