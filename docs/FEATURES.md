# Features

## Home Screen

- Displays a curated list of tracked cryptocurrency pairs (hardcoded in `CryptoRepository.getTrackedPairs()`)
- **Pull-to-refresh** via `PullToRefreshBox`
- **Auto-refresh** every 30 seconds (started via `LaunchedEffect`)
- **Top Boosted Tokens** horizontal carousel at the top
- **Settings gear icon** in the TopAppBar (navigates to Settings)
- Error state display when data fails to load

### Top Boosted Tokens Carousel

- Fetches top 10 tokens from DexScreener token-boosts API
- Displays as a horizontal `LazyRow` of `BoostedTokenCard` components
- Each card (200x100dp) shows:
  - Banner image as background (from `header` URL)
  - Token name centered with gradient overlay
  - No icon, no amount displayed
- Tapping a card navigates to the full detail screen with price/chart data
- Failed token resolutions are silently skipped (404 handling)

## Search Screen

- Search input with **500ms debounce** (prevents excessive API calls)
- Results displayed as a list of `CryptoItemCard` components
- Tapping a result navigates to the Detail screen
- Empty state when no query is entered

## Detail Screen

- Displays comprehensive token data:
  - Current price (USD)
  - 24h price change percentage
  - 24h volume
  - Liquidity (USD)
  - Fully Diluted Valuation (FDV)
  - Market Cap
  - Chain and DEX information
  - Token image (via Coil)
- **Favorite toggle** -- add/remove from favorites (persisted in Room DB)
- **Portfolio toggle** -- add to portfolio with buy price and quantity input
- **TradingView chart** embedded via WebView (DexScreener chart URL)
- **Auto-refresh** prices every 30 seconds
- Back navigation via TopAppBar arrow

## Favorites Screen

- Lists all saved favorites from Room database
- Each favorite is fetched fresh from the API on display
- **Reactive refresh** -- updates immediately when favorites are added/removed (via Room Flow observation)
- **Auto-refresh** prices every 30 seconds
- **Pull-to-refresh** support
- Tapping an item navigates to Detail screen
- Empty state when no favorites are saved

## Portfolio Screen

- Lists all holdings from Room database
- **Portfolio Summary Card** at the top showing:
  - Total value (sum of all holdings at current price)
  - Total P&L in USD
  - Total P&L percentage
- Each holding card shows:
  - Token image (via Coil)
  - Token symbol and name
  - Quantity x buy price
  - Current value
  - P&L in USD and percentage (green for profit, red for loss)
- **Swipe-to-delete** (SwipeToDismissBox, left-to-right only)
  - Red background with trash icon during swipe
  - Rounded corners (16dp)
  - Triggers confirmation `AlertDialog`
- **Reactive refresh** -- updates immediately when holdings are added/removed
- **Auto-refresh** prices every 30 seconds
- **Pull-to-refresh** support
- Empty state when portfolio is empty

## Settings Screen

- **Theme switching** with three options:
  - System (default) -- follows device theme
  - Light -- forced light theme
  - Dark -- forced dark theme
- Options displayed as `RadioButton` list
- **DataStore persistence** -- theme choice survives app restarts
- **Back navigation** via TopAppBar arrow
- **About section** with "Created By: Nort" (no version number)
- Theme is applied app-wide via `CryptoTrackerTheme` composable in `MainActivity`

### Theme Data Flow

```
SettingsScreen -> SettingsViewModel.setThemeMode() -> ThemePreference (DataStore) -> MainActivity reads ThemeMode -> CryptoTrackerTheme
```

## Navigation

- **Bottom Navigation Bar** with 4 tabs:
  - Home (house icon)
  - Search (search icon)
  - Portfolio (wallet icon)
  - Favorites (heart icon)
- Bottom bar is hidden on Detail and Settings screens
- Settings accessible via gear icon on Home screen TopAppBar
- Detail screen accessible from Home, Search, Favorites, and Boosted Tokens
