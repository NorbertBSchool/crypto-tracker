# API

## Base URL

```
https://api.dexscreener.com/
```

## Endpoints Used

### 1. Get Pair Details

```
GET /latest/dex/pairs/{chainId}/{pairAddress}
```

Returns detailed information for a specific trading pair.

**Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| `chainId` | string | Blockchain identifier (e.g., `ethereum`, `solana`) |
| `pairAddress` | string | Trading pair contract address |

**Response:** `DexScreenerResponse` wrapping a single `PairData` object.

---

### 2. Search Tokens

```
GET /latest/dex/search?q={query}
```

Search for tokens by name or symbol.

**Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| `query` | string | Search query (token name or symbol) |

**Response:** `DexScreenerResponse` wrapping a list of `PairData` objects.

---

### 3. Get Token Boosts

```
GET /token-boosts/top/v1
```

Returns the top boosted tokens on DexScreener.

**Response:** List of `TokenBoostResponse` objects:

```json
[
  {
    "chainId": "solana",
    "tokenAddress": "abc123...",
    "header": "https://example.com/banner.jpg",
    "icon": "boost-icon-id",
    "description": "Token description",
    "amount": 1500,
    "totalAmount": 1500
  }
]
```

**Notes:**
- `header` is a banner image URL (not a name)
- `icon` is just an ID string (not a full URL)
- `description` can be null
- `amount` and `totalAmount` are boost counts

---

### 4. Get Tokens by Address

```
GET /token-pairs/v1/{chainId}/{tokenAddress}
```

Returns trading pairs for a specific token address. Used to resolve boosted tokens into full pair data.

**Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| `chainId` | string | Blockchain identifier |
| `tokenAddress` | string | Token contract address |

**Response:** `List<PairData>` (raw array, not wrapped in DexScreenerResponse)

**Important:** This endpoint returns a raw `List<PairData>` unlike other endpoints which wrap in `DexScreenerResponse`.

---

## Data Models

### PairData

| Field | Type | Description |
|-------|------|-------------|
| `pairAddress` | String | Unique pair identifier |
| `chainId` | String | Blockchain |
| `dexId` | String | DEX identifier |
| `baseToken` | Token | Base token info (symbol, name, address) |
| `quoteToken` | Token | Quote token info |
| `priceUsd` | String? | Current USD price |
| `priceNative` | String? | Native chain price |
| `priceChange` | Map? | Price changes (h1, h6, h24) |
| `volume` | Map? | Volume data (h24) |
| `liquidity` | Liquidity? | Liquidity in USD |
| `fdv` | Double? | Fully diluted valuation |
| `marketCap` | Double? | Market cap |
| `url` | String? | DexScreener URL |
| `info` | Info? | Token info (imageUrl) |

### TokenBoostResponse

| Field | Type | Description |
|-------|------|-------------|
| `chainId` | String | Blockchain |
| `tokenAddress` | String | Token address |
| `header` | String? | Banner image URL |
| `icon` | String? | Icon ID |
| `description` | String? | Token description |
| `amount` | Int | Boost count |
| `totalAmount` | Int | Total boost count |

### CryptoCurrency (Domain Model)

| Field | Type | Description |
|-------|------|-------------|
| `pairAddress` | String | Pair identifier |
| `chainId` | String | Blockchain |
| `dexId` | String | DEX |
| `baseTokenSymbol` | String | Token symbol |
| `baseTokenName` | String | Token name |
| `baseTokenAddress` | String | Token address |
| `quoteTokenSymbol` | String | Quote symbol |
| `priceUsd` | String | USD price |
| `priceChange24h` | Double? | 24h price change % |
| `volume24h` | Double? | 24h volume |
| `liquidityUsd` | Double? | Liquidity USD |
| `fdv` | Double? | FDV |
| `marketCap` | Double? | Market cap |
| `url` | String? | DexScreener URL |
| `imageUrl` | String? | Token image URL |

---

## Error Handling

All API calls are wrapped in try-catch blocks in the Repository. Failed calls return `Result.failure(exception)` or empty lists. The UI displays error messages from `UiState.error`.
