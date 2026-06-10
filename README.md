# CryptoTracker

Android cryptocurrency tracking app built with Jetpack Compose, MVVM architecture, and Material 3 Design.

## Documentation

| Document | Description |
|----------|-------------|
| [Architecture](docs/ARCHITECTURE.md) | MVVM + Clean Architecture, layer design, data flow |
| [API](docs/API.md) | DexScreener endpoints, request/response formats |
| [Features](docs/FEATURES.md) | Detailed feature descriptions and UX patterns |
| [Testing](docs/TESTING.md) | TDD approach, unit and instrumented tests |
| [Setup](docs/SETUP.md) | Prerequisites, build instructions, running the app |
| [Dependencies](docs/DEPENDENCIES.md) | Full dependency table with versions |

## Architecture

**MVVM + Clean Architecture** with full layer separation:

```
UI (Compose + ViewModel) -> Repository -> API (Retrofit) / DB (Room)
```

### Data Layer
- **`data/remote/`** -- DexScreener API (Retrofit), API data models
- **`data/local/`** -- Room database (FavoriteEntity, FavoriteDao, FavoriteDatabase), DataStore (ThemePreference)
- **`data/repository/`** -- CryptoRepository (single source of truth for all data operations)

### Domain Layer
- **`domain/model/`** -- CryptoCurrency, PortfolioItem, TokenBoostItem

### UI Layer
- **`ui/home/`** -- HomeScreen + HomeViewModel
- **`ui/search/`** -- SearchScreen + SearchViewModel
- **`ui/detail/`** -- DetailScreen + DetailViewModel
- **`ui/favorites/`** -- FavoritesScreen + FavoritesViewModel
- **`ui/portfolio/`** -- PortfolioScreen + PortfolioViewModel
- **`ui/settings/`** -- SettingsScreen + SettingsViewModel
- **`ui/navigation/`** -- Bottom navigation, screen routes
- **`ui/components/`** -- Reusable CryptoItemCard, BoostedTokenCard
- **`ui/theme/`** -- Material 3 theme with System/Light/Dark support

### DI Layer
- **`di/`** -- Hilt modules (AppModule for Retrofit/OkHttp, DatabaseModule for Room)

## Features

- **Home** -- Live-tracked crypto pairs with pull-to-refresh, auto-refresh every 30 seconds, top boosted tokens carousel
- **Search** -- Search any token via DexScreener API with debounced input (500ms)
- **Detail** -- Price, volume, liquidity, FDV, market cap, chain/DEX info, favorite toggle, portfolio add, DexScreener TradingView chart embed (WebView), auto-refresh prices every 30 seconds
- **Favorites** -- Locally saved favorites via Room database, reactive refresh, auto-refresh prices every 30 seconds
- **Portfolio** -- Track holdings with buy price, quantity, P&L calculations, swipe-to-delete with confirmation
- **Settings** -- Theme switching (System / Light / Dark) persisted via DataStore, "Created By: Nort"
- **Boosted Tokens** -- Horizontal carousel of top 10 DexScreener-boosted tokens with banner images
- **Avatars** -- Token images loaded from DexScreener API via Coil (with fallback to symbol text)
- **Navigation** -- Bottom nav bar (Home / Search / Portfolio / Favorites)

## API

Uses [DexScreener API](https://docs.dexscreener.com/api/reference):
- `GET /latest/dex/pairs/{chainId}/{pairAddress}` -- pair details
- `GET /latest/dex/search?q={query}` -- token search
- `GET /token-boosts/top/v1` -- top boosted tokens
- `GET /token-pairs/v1/{chainId}/{tokenAddress}` -- token pairs by address

## Testing

### Unit Tests (JUnit 4 + Mockito)
~20 tests total, all with mocked repository for isolation:

| Test Class | Tests | Coverage |
|---|---|---|
| `HomeViewModelTest` | 3 | loadCryptoData: success, empty result, exception |
| `SearchViewModelTest` | 5 | initial state, query update, blank query, search success, search failure |
| `DetailViewModelTest` | 4 | loadDetails success, loadDetails failure, observeFavorite, toggleFavorite |
| `FavoritesViewModelTest` | 3 | loadFavorites success, loadFavorites empty, loadFavorites exception |
| `PortfolioViewModelTest` | 3+ | loadPortfolio success, loadPortfolio empty, removeFromPortfolio |

### Instrumented Tests (Compose Testing + Espresso)
5 tests with `@HiltAndroidTest` and `createComposeRule()`:

| Test | Coverage |
|---|---|
| `appLaunches` | App starts and shows "CryptoTracker" title |
| `bottomNav_showsAllTabs` | Home, Search, Portfolio, Favorites tabs visible |
| `navigateToSearch_showsSearchBar` | Search screen displays search input |
| `navigateToFavorites_showsTitle` | Favorites screen shows empty state |
| `navigateToSearch_thenHome_showsCryptoTracker` | Navigation round-trip works |

### TDD Approach
Tests were written alongside feature implementation (RED -> GREEN -> REFACTOR):
1. **ViewModel tests** define expected state transitions (loading -> success/error) before UI is built
2. **Mockito** isolates business logic from network/database dependencies
3. **Compose tests** verify navigation and screen rendering without real API calls
4. Auto-refresh features designed with testability in mind (init-only load, LaunchedEffect triggers refresh loop)

See [Testing Documentation](docs/TESTING.md) for detailed TDD analysis.

## Tech Stack

| Library | Version | Purpose |
|---|---|---|
| Kotlin | 2.3.21 | Language |
| Compose BOM | 2026.05.01 | UI framework |
| Material 3 | -- | Design system |
| Hilt | 2.58 | Dependency injection |
| Retrofit | 3.0.0 | HTTP client |
| OkHttp | 5.3.2 | HTTP logging |
| Room | 2.7.0 | Local database |
| DataStore | 1.1.7 | Theme persistence |
| Coil | 2.7.0 | Image loading (token avatars) |
| WebKit | 1.12.1 | WebView (DexScreener chart embed) |
| Navigation Compose | 2.7.7 | Screen navigation |
| Coroutines | 1.11.0 | Async operations |
| JUnit 4 | 4.13.2 | Unit testing |
| Mockito | 5.11.0 | Mocking |
| Mockito-Kotlin | 5.3.1 | Kotlin Mockito extensions |
| Turbine | 1.1.0 | Flow testing |
| Espresso | 3.7.0 | UI testing |

## Project Structure

```
app/src/main/java/com/example/cryptotracker/
├── CryptoTracker.kt          # Hilt Application
├── MainActivity.kt           # Entry point
├── data/
│   ├── local/                # Room: entities, DAOs, Database; DataStore: ThemePreference
│   ├── remote/               # DexScreenerApi, API models (PairData, TokenBoostResponse, etc.)
│   └── repository/           # CryptoRepository
├── di/                       # Hilt: AppModule, DatabaseModule
├── domain/
│   └── model/                # CryptoCurrency, PortfolioItem, TokenBoostItem
└── ui/
    ├── components/            # CryptoItemCard, BoostedTokenCard (reusable)
    ├── detail/                # DetailScreen, DetailViewModel
    ├── favorites/             # FavoritesScreen, FavoritesViewModel
    ├── home/                  # HomeScreen, HomeViewModel
    ├── navigation/            # Screen routes, CryptoTrackerNavGraph
    ├── portfolio/             # PortfolioScreen, PortfolioViewModel
    ├── search/                # SearchScreen, SearchViewModel
    ├── settings/              # SettingsScreen, SettingsViewModel
    └── theme/                 # Material 3 theme with DataStore persistence
```

## How to Run

```bash
./gradlew assembleDebug
```

Or open in Android Studio and click Run.

## Author

Created by **Nort**
