package com.example.cryptotracker

import com.example.cryptotracker.data.remote.model.Liquidity
import com.example.cryptotracker.data.remote.model.PairData
import com.example.cryptotracker.data.remote.model.PairInfo
import com.example.cryptotracker.data.remote.model.Token
import com.example.cryptotracker.data.repository.toCryptoCurrency
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DataMappingTest {

    private fun createPairData(
        chainId: String = "ethereum",
        dexId: String = "uniswap",
        pairAddress: String = "0xabc",
        baseTokenSymbol: String = "USDC",
        baseTokenName: String = "USD Coin",
        baseTokenAddress: String = "0xdef",
        quoteTokenSymbol: String = "WETH",
        priceUsd: String? = "1.0",
        priceNative: String = "0.0005",
        priceChange: Map<String, Double>? = mapOf("h1" to 0.1, "h24" to 2.5),
        volume: Map<String, Double>? = mapOf("h24" to 500000.0),
        liquidity: Liquidity? = Liquidity(usd = 1000000.0, base = null, quote = null),
        fdv: Double? = 5000000.0,
        marketCap: Double? = 3000000.0,
        url: String? = "https://dexscreener.com/ethereum/0xabc",
        info: PairInfo? = PairInfo(imageUrl = "https://img.com/logo.png", null, null)
    ) = PairData(
        chainId = chainId,
        dexId = dexId,
        url = url,
        pairAddress = pairAddress,
        baseToken = Token(address = baseTokenAddress, name = baseTokenName, symbol = baseTokenSymbol),
        quoteToken = Token(address = "0x0", name = "Wrapped Ether", symbol = quoteTokenSymbol),
        priceNative = priceNative,
        priceUsd = priceUsd,
        txns = null,
        volume = volume,
        priceChange = priceChange,
        liquidity = liquidity,
        fdv = fdv,
        marketCap = marketCap,
        pairCreatedAt = null,
        info = info
    )

    @Test
    fun `maps basic fields correctly`() {
        val pair = createPairData()
        val currency = pair.toCryptoCurrency()

        assertEquals("0xabc", currency.pairAddress)
        assertEquals("ethereum", currency.chainId)
        assertEquals("uniswap", currency.dexId)
        assertEquals("USDC", currency.baseTokenSymbol)
        assertEquals("USD Coin", currency.baseTokenName)
        assertEquals("0xdef", currency.baseTokenAddress)
        assertEquals("WETH", currency.quoteTokenSymbol)
        assertEquals("1.0", currency.priceUsd)
        assertEquals("0.0005", currency.priceNative)
    }

    @Test
    fun `maps nested priceChange and volume correctly`() {
        val pair = createPairData()
        val currency = pair.toCryptoCurrency()

        assertEquals(0.1, currency.priceChange1h!!, 0.001)
        assertEquals(2.5, currency.priceChange24h!!, 0.001)
        assertEquals(500000.0, currency.volume24h!!, 0.01)
    }

    @Test
    fun `maps nullable fields to null when absent`() {
        val pair = createPairData(
            priceUsd = null,
            priceChange = null,
            volume = null,
            liquidity = null,
            fdv = null,
            marketCap = null,
            url = null,
            info = null
        )
        val currency = pair.toCryptoCurrency()

        assertEquals("0", currency.priceUsd)
        assertNull(currency.priceChange24h)
        assertNull(currency.priceChange1h)
        assertNull(currency.volume24h)
        assertNull(currency.liquidityUsd)
        assertNull(currency.fdv)
        assertNull(currency.marketCap)
        assertNull(currency.url)
        assertNull(currency.imageUrl)
    }

    @Test
    fun `maps liquidity usd correctly`() {
        val pair = createPairData()
        val currency = pair.toCryptoCurrency()

        assertEquals(1000000.0, currency.liquidityUsd!!, 0.01)
    }

    @Test
    fun `maps image url from info`() {
        val pair = createPairData()
        val currency = pair.toCryptoCurrency()

        assertEquals("https://img.com/logo.png", currency.imageUrl)
    }
}
