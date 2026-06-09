package com.example.cryptotracker.data.remote

import com.example.cryptotracker.data.remote.model.DexScreenerResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DexScreenerApi {

    @GET("latest/dex/pairs/{chainId}/{pairAddress}")
    suspend fun getPair(
        @Path("chainId") chainId: String,
        @Path("pairAddress") pairAddress: String
    ): DexScreenerResponse

    @GET("latest/dex/search")
    suspend fun searchPairs(
        @Query("q") query: String
    ): DexScreenerResponse

    @GET("latest/dex/tokens/{chainId}/{tokenAddress}")
    suspend fun getTokensByAddress(
        @Path("chainId") chainId: String,
        @Path("tokenAddress") tokenAddress: String
    ): DexScreenerResponse
}
