# Architecture

## Overview

CryptoTracker follows **MVVM + Clean Architecture** with clear separation between UI, domain, and data layers.

```
+-----------------------------------------+
|               UI Layer                  |
|  Compose Screens + ViewModels           |
|  (Home, Search, Detail, Favorites,      |
|   Portfolio, Settings)                  |
+----------------+------------------------+
                 | observes StateFlow / SharedFlow
+----------------v------------------------+
|            Data Layer                   |
|  CryptoRepository (single source)       |
+----------------+------------------------+
|  Remote         |  Local                |
|  DexScreener    |  Room DB              |
|  API/Retrofit   |  FavoriteDao/HoldingDao|
+-----------------+-----------------------+
```

## Layers

### UI Layer (`ui/`)

Each screen has a **Composable** function and a **ViewModel**:

| Package | Screen | ViewModel |
|---------|--------|-----------|
| `ui/home/` | HomeScreen | HomeViewModel |
| `ui/search/` | SearchScreen | SearchViewModel |
| `ui/detail/` | DetailScreen | DetailViewModel |
| `ui/favorites/` | FavoritesScreen | FavoritesViewModel |
| `ui/portfolio/` | PortfolioScreen | PortfolioViewModel |
| `ui/settings/` | SettingsScreen | SettingsViewModel |

**State management:**
- ViewModels expose `StateFlow<UiState>` for screen state
- `SharedFlow<CryptoCurrency>` for one-shot navigation events (e.g., `navigateToDetail`)
- Composables collect state via `collectAsState()`

**Auto-refresh pattern:**
- `startAutoRefresh()` launches a coroutine loop with `delay(30_000)`
- Called from `LaunchedEffect(Unit)` in the Composable
- Refresh interval: 30 seconds

### Data Layer

#### CryptoRepository (`data/repository/CryptoRepository.kt`)

Single source of truth for all data operations. Methods:

- `getTrackedPairs()` -- returns hardcoded list of tracked tokens
- `searchTokens(query)` -- search via DexScreener
- `getPairData(chainId, pairAddress)` -- single pair details
- `getTopBoostedTokens()` -- top 10 boosted tokens (parallel API calls)
- `resolveBoostedToken(chainId, tokenAddress)` -- resolve boosted token to CryptoCurrency
- `getFavorites()` / `isFavorite()` / `toggleFavorite()` -- Room-backed favorites
- `getPortfolioItems()` / `addHolding()` / `removeHolding()` -- Room-backed portfolio

#### Data Mapping

`PairData.toCryptoCurrency()` is a top-level `internal` extension function that converts DexScreener API models to domain models. Extracted as `internal` (instead of `private`) to enable unit testing via `DataMappingTest`.

#### Remote Data (`data/remote/`)

- `DexScreenerApi` -- Retrofit interface for DexScreener API
- API models: `DexScreenerResponse`, `PairData`, `TokenBoostResponse`, etc.

#### Local Data (`data/local/`)

- `FavoriteDatabase` -- Room database (version 2, with migration 1->2)
- `FavoriteEntity` / `FavoriteDao` -- favorites table
- `HoldingEntity` / `HoldingDao` -- portfolio holdings table
- `ThemePreference` -- DataStore-based theme persistence

### Domain Layer (`domain/model/`)

Business models:
- `CryptoCurrency` -- token pair data (price, volume, liquidity, etc.)
- `PortfolioItem` -- holding with buy price, quantity, current price, P&L
- `TokenBoostItem` -- boosted token (chainId, tokenAddress, name, bannerUrl)

## Dependency Injection

Hilt with `@HiltViewModel` and `@Inject constructor`:

- `AppModule` -- provides OkHttp, Retrofit, DexScreenerApi (SingletonComponent)
- `DatabaseModule` -- provides Room database, DAOs (SingletonComponent)

## Navigation

Navigation Compose with bottom bar:
- **Home** -> **Search** -> **Portfolio** -> **Favorites** (bottom nav)
- **Detail** (full-screen, parameterized: `detail/{pairAddress}/{chainId}`)
- **Settings** (full-screen, no bottom bar)

Routes defined in `Screen.kt` as sealed class objects.

## Data Flow

1. **User action** -> ViewModel method called
2. **ViewModel** -> calls Repository
3. **Repository** -> calls API + queries Room DB
4. **Repository** -> returns data to ViewModel
5. **ViewModel** -> updates `MutableStateFlow<UiState>`
6. **Compose** -> recomposes with new state

For reactive updates (favorites/portfolio):
- Room DAOs return `Flow<List<T>>`
- ViewModels observe Flows with `distinctUntilChanged`
- DB changes trigger Flow emission -> ViewModel reloads API data -> UI updates

## Reactive Refresh Pattern

```
Room DAO (Flow) -> ViewModel (collectAsState) -> triggers API reload -> updates UI StateFlow -> Compose recomposes
```

This ensures favorites/portfolio update immediately after adding/removing items, without requiring manual refresh.
