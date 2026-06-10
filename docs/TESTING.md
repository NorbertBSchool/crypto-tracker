# Testing

## Overview

CryptoTracker uses **TDD (Test-Driven Development)** with unit tests (JUnit 4 + Mockito) and instrumented tests (Compose Testing v2).

**Total: 35 unit tests + 6 instrumented tests**

## TDD Approach

Development followed the **RED -> GREEN -> REFACTOR** cycle:

1. **RED** -- Write a failing test that defines expected behavior
2. **GREEN** -- Write minimal code to make the test pass
3. **REFACTOR** -- Clean up code while keeping tests green

### How Tests Influenced Design

- **ViewModel tests** were written first, defining expected state transitions (loading -> success/error) before UI implementation
- **Repository interface** was shaped by what ViewModels needed to mock
- **StateFlow patterns** emerged from testing requirements -- tests needed deterministic state to assert against
- **Auto-refresh design** was driven by testability: init-only load in ViewModel, `LaunchedEffect` triggers refresh loop in Composable
- **Parallel API calls** in `HomeViewModel.loadCryptoData()` were designed to be testable with individual async job mocking
- **Boosted token error handling** -- each async body has its own try-catch to prevent one failure from cancelling siblings (discovered during testing)
- **`PairData.toCryptoCurrency()`** was extracted as a top-level internal function specifically to enable unit testing of JSON-to-domain mapping logic
- **`PortfolioItem` computed properties** (totalCost, currentValue, pnlUsd, pnlPercent) were designed with zero-cost guards to prevent division-by-zero

### Key Testing Decisions

| Decision | Reason |
|----------|--------|
| Mockito for Repository mocking | Isolates ViewModel logic from network/database |
| `StateFlow` for UI state | Testable with Turbine, deterministic emission |
| `SharedFlow` for navigation events | One-shot events need replay=0 to avoid re-emission |
| `distinctUntilChanged` on DB Flows | Prevents unnecessary API reloads on duplicate emissions |
| Per-async try-catch in `getTopBoostedTokens()` | Prevents coroutineScope cancellation when one token fails |
| `internal` for `toCryptoCurrency()` | Enables unit testing of mapping logic without exposing as public API |
| JaCoCo for coverage | Configured for generating code coverage reports |

## Unit Tests (JUnit 4 + Mockito)

**35 tests** across 8 test classes, all with mocked repository for isolation.

### HomeViewModelTest (3 tests)

| Test | Description |
|------|-------------|
| `loadCryptoData success` | Verifies state updates with data on successful load |
| `loadCryptoData empty result` | Verifies "No data available" error when both lists empty |
| `loadCryptoData exception` | Verifies error state when repository throws |

### SearchViewModelTest (5 tests)

| Test | Description |
|------|-------------|
| `initial state` | Verifies empty state on creation |
| `update query` | Verifies query updates in state |
| `blank query ignored` | Verifies blank/whitespace queries are skipped |
| `search success` | Verifies results populate state |
| `search failure` | Verifies error message on API failure |

### DetailViewModelTest (7 tests)

| Test | Description |
|------|-------------|
| `loadDetails success` | Verifies price and token data load correctly |
| `loadDetails failure` | Verifies error state on API failure |
| `observeFavorite` | Verifies favorite state reflects DB |
| `toggleFavorite` | Verifies toggle calls repository correctly |
| `addToPortfolio success` | Verifies addHolding called with correct parameters |
| `addToPortfolio null crypto` | Verifies no-op when crypto data not loaded |
| `removeFromPortfolio` | Verifies removeHolding called with correct pairAddress |

### FavoritesViewModelTest (3 tests)

| Test | Description |
|------|-------------|
| `loadFavorites success` | Verifies favorites list populated |
| `loadFavorites empty` | Verifies empty state message |
| `loadFavorites exception` | Verifies error state on failure |

### PortfolioViewModelTest (5 tests)

| Test | Description |
|------|-------------|
| `loadPortfolio success` | Verifies portfolio items and P&L calculations |
| `loadPortfolio empty` | Verifies zero totals on empty portfolio |
| `loadPortfolio zero cost items` | Verifies zero-cost guard prevents division-by-zero in PNL percent |
| `loadPortfolio exception` | Verifies error state on failure |
| `removeFromPortfolio` | Verifies holding removal calls repository |

### PortfolioItemTest (6 tests)

Tests computed properties on the `PortfolioItem` domain model:

| Test | Description |
|------|-------------|
| `totalCost multiplies buyPrice by quantity` | Verifies buyPriceUsd * quantity |
| `currentValue multiplies currentPrice by quantity` | Verifies currentPriceUsd * quantity |
| `pnlUsd is difference between currentValue and totalCost` | Verifies currentValue - totalCost |
| `pnlPercent calculates percentage gain correctly` | Verifies positive percentage change |
| `pnlPercent calculates percentage loss correctly` | Verifies negative percentage change |
| `pnlPercent returns 0 when buyPrice is 0` | Verifies zero-cost division-by-zero guard |

### DataMappingTest (5 tests)

Tests the `PairData.toCryptoCurrency()` extension function (JSON API model to domain model):

| Test | Description |
|------|-------------|
| `maps basic fields correctly` | Verifies pairAddress, chainId, dexId, token symbols, prices |
| `maps nested priceChange and volume correctly` | Verifies priceChange.get("h24"), volume.get("h24") extraction |
| `maps nullable fields to null when absent` | Verifies null safety for priceUsd, priceChange, volume, liquidity, fdv, etc. |
| `maps liquidity usd correctly` | Verifies liquidity.usd extraction |
| `maps image url from info` | Verifies info.imageUrl extraction |

### ExampleUnitTest (1 test)

Boilerplate placeholder test (not business logic).

### Mock Setup Pattern

All ViewModel tests mock `CryptoRepository` in `@Before setup()`. ViewModels that observe Room Flows in `init` (Favorites, Portfolio, Home) require `runBlocking { whenever(...) }` in setup because suspend function mocking doesn't work in non-suspend `setup()`.

```kotlin
@Before
fun setup() {
    repository = mock()
    runBlocking {
        whenever(repository.getFavorites()).thenReturn(flowOf(emptyList()))
        whenever(repository.getHoldingsFlow()).thenReturn(flowOf(emptyList()))
        whenever(repository.getTopBoostedTokens()).thenReturn(emptyList())
    }
}
```

## Instrumented Tests (Compose Testing v2)

**6 tests** using `createComposeRule()` from `androidx.compose.ui.test.junit4.v2`.

### NavigationTest (5 tests)

Uses a `TestNavGraph()` composable that replicates the same Scaffold + NavigationBar + NavHost structure as `CryptoTrackerNavGraph()`, but with placeholder composables (no Hilt ViewModel dependency). This avoids Hilt Activity resolution issues while testing the same navigation structure.

| Test | Description |
|------|-------------|
| `appLaunches` | App starts and shows "CryptoTracker" title |
| `bottomNav_showsAllTabs` | Home, Search, Favorites tabs visible in bottom nav |
| `navigateToSearch_showsSearchBar` | Clicking Search tab navigates to search placeholder content |
| `navigateToFavorites_showsTitle` | Clicking Favorites tab navigates to "No favorites yet" content |
| `navigateToSearch_thenHome_showsCryptoTracker` | Navigation round-trip works (Search -> Home) |

### ExampleInstrumentedTest (1 test)

Boilerplate context test verifying package name.

### Test Infrastructure

| Component | Details |
|-----------|---------|
| **Test runner** | `AndroidJUnitRunner` (default) |
| **Compose rule** | `createComposeRule()` from v2 package (uses `StandardTestDispatcher`) |
| **Hilt in tests** | Not used in instrumented tests. `hilt-android-testing` dependency remains available for future `@HiltAndroidTest` classes |

### Why NavigationTest Doesn't Use Hilt

Hilt instrumented tests with Compose require a `@AndroidEntryPoint` Activity hosted via `createAndroidComposeRule<T>()`. However, Hilt 2.58 + modern AndroidX Test has compatibility issues where `InstrumentationActivityInvoker` cannot resolve the test Activity. The solution is to test navigation structure independently using a `TestNavGraph()` that mirrors the production nav graph without requiring Hilt dependency injection.

## Code Coverage (JaCoCo)

JaCoCo is configured for unit test coverage reports.

```bash
# Generate coverage report
./gradlew jacocoTestReport
```

Reports generated at: `app/build/reports/jacoco/testDebugUnitTest/html/index.html`

Coverage excludes: R classes, BuildConfig, Hilt-generated factories and components.

## Running Tests

```bash
# Unit tests (35 tests)
./gradlew testDebugUnitTest

# Instrumented tests (6 tests, requires device/emulator)
./gradlew connectedDebugAndroidTest

# Coverage report
./gradlew jacocoTestReport
```
