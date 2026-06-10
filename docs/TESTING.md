# Testing

## Overview

CryptoTracker uses **TDD (Test-Driven Development)** with unit tests (JUnit 4 + Mockito) and instrumented tests (Compose Testing + Espresso).

**Total: ~20 unit tests + 5 instrumented tests**

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

### Key Testing Decisions

| Decision | Reason |
|----------|--------|
| Mockito for Repository mocking | Isolates ViewModel logic from network/database |
| `StateFlow` for UI state | Testable with Turbine, deterministic emission |
| `SharedFlow` for navigation events | One-shot events need replay=0 to avoid re-emission |
| `distinctUntilChanged` on DB Flows | Prevents unnecessary API reloads on duplicate emissions |
| Per-async try-catch in `getTopBoostedTokens()` | Prevents coroutineScope cancellation when one token fails |

## Unit Tests (JUnit 4 + Mockito)

### HomeViewModelTest

| Test | Description |
|------|-------------|
| `loadCryptoData success` | Verifies state updates with data on successful load |
| `loadCryptoData empty result` | Verifies "No data available" error when both lists empty |
| `loadCryptoData exception` | Verifies error state when repository throws |

### SearchViewModelTest

| Test | Description |
|------|-------------|
| `initial state` | Verifies empty state on creation |
| `update query` | Verifies query updates in state |
| `blank query ignored` | Verifies blank/whitespace queries are skipped |
| `search success` | Verifies results populate state |
| `search failure` | Verifies error message on API failure |

### DetailViewModelTest

| Test | Description |
|------|-------------|
| `loadDetails success` | Verifies price and token data load correctly |
| `loadDetails failure` | Verifies error state on API failure |
| `observeFavorite` | Verifies favorite state reflects DB |
| `toggleFavorite` | Verifies toggle calls repository correctly |

### FavoritesViewModelTest

| Test | Description |
|------|-------------|
| `loadFavorites success` | Verifies favorites list populated |
| `loadFavorites empty` | Verifies empty state message |
| `loadFavorites exception` | Verifies error state on failure |

### PortfolioViewModelTest

| Test | Description |
|------|-------------|
| `loadPortfolio success` | Verifies portfolio items and P&L calculations |
| `loadPortfolio empty` | Verifies empty state |
| `removeFromPortfolio` | Verifies holding removal |

### Mock Setup Pattern

All tests mock `CryptoRepository` in `@Before setup()`. ViewModels that observe Room Flows in `init` (Favorites, Portfolio, Home) require `runBlocking { whenever(...) }` in setup because suspend function mocking doesn't work in non-suspend `setup()`.

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

## Instrumented Tests (Compose + Espresso)

Uses `@HiltAndroidTest` with `createComposeRule()`.

| Test | Description |
|------|-------------|
| `appLaunches` | App starts and shows "CryptoTracker" title |
| `bottomNav_showsAllTabs` | Home, Search, Portfolio, Favorites tabs visible |
| `navigateToSearch_showsSearchBar` | Search screen displays search input |
| `navigateToFavorites_showsTitle` | Favorites screen shows empty state |
| `navigateToSearch_thenHome_showsCryptoTracker` | Navigation round-trip works |

### Hilt Test Configuration

- **Test runner:** `AndroidJUnitRunner` (not HiltTestRunner, which was removed in Hilt 2.58)
- **Test application:** `TestApplication` extends `HiltTestApplication`
- **Manifest:** `androidTest/AndroidManifest.xml` with `android:name=".TestApplication"`

## Running Tests

```bash
# Unit tests
./gradlew test

# Instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest
```
