package com.example.cryptotracker

import com.example.cryptotracker.domain.model.PortfolioItem
import org.junit.Assert.assertEquals
import org.junit.Test

class PortfolioItemTest {

    private fun createItem(
        buyPriceUsd: Double = 1.0,
        quantity: Double = 100.0,
        currentPriceUsd: Double = 1.5
    ) = PortfolioItem(
        pairAddress = "0x123",
        chainId = "ethereum",
        dexId = "uniswap",
        tokenSymbol = "USDC",
        tokenName = "USD Coin",
        quoteTokenSymbol = "WETH",
        buyPriceUsd = buyPriceUsd,
        quantity = quantity,
        currentPriceUsd = currentPriceUsd,
        imageUrl = null
    )

    @Test
    fun `totalCost multiplies buyPrice by quantity`() {
        val item = createItem(buyPriceUsd = 2.5, quantity = 40.0)
        assertEquals(100.0, item.totalCost, 0.001)
    }

    @Test
    fun `currentValue multiplies currentPrice by quantity`() {
        val item = createItem(quantity = 200.0, currentPriceUsd = 3.0)
        assertEquals(600.0, item.currentValue, 0.001)
    }

    @Test
    fun `pnlUsd is difference between currentValue and totalCost`() {
        val item = createItem(buyPriceUsd = 1.0, quantity = 100.0, currentPriceUsd = 1.5)
        assertEquals(50.0, item.pnlUsd, 0.001)
    }

    @Test
    fun `pnlPercent calculates percentage gain correctly`() {
        val item = createItem(buyPriceUsd = 1.0, quantity = 100.0, currentPriceUsd = 1.5)
        assertEquals(50.0, item.pnlPercent, 0.001)
    }

    @Test
    fun `pnlPercent calculates percentage loss correctly`() {
        val item = createItem(buyPriceUsd = 2.0, quantity = 50.0, currentPriceUsd = 1.0)
        assertEquals(-50.0, item.pnlPercent, 0.001)
    }

    @Test
    fun `pnlPercent returns 0 when buyPrice is 0`() {
        val item = createItem(buyPriceUsd = 0.0, quantity = 100.0, currentPriceUsd = 0.5)
        assertEquals(0.0, item.pnlPercent, 0.001)
    }
}
