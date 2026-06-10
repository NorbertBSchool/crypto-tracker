# CryptoTracker Documentation

A modern Android cryptocurrency tracking application built with Jetpack Compose, MVVM architecture, and Material 3 Design.

## Overview

CryptoTracker allows users to monitor live cryptocurrency pair data, manage a favorites list, track a personal portfolio with profit/loss calculations, and discover trending tokens through DexScreener's boosted token rankings.

## Features

- **Home** -- Live-tracked crypto pairs with pull-to-refresh, auto-refresh every 30 seconds, top boosted tokens carousel
- **Search** -- Search any token via DexScreener API with debounced input (500ms)
- **Detail** -- Price, volume, liquidity, FDV, market cap, chain/DEX info, favorite toggle, portfolio add, DexScreener TradingView chart (WebView)
- **Favorites** -- Locally saved favorites via Room database, reactive refresh, auto-refresh prices
- **Portfolio** -- Track holdings with buy price, quantity, P&L calculations, swipe-to-delete with confirmation
- **Settings** -- Theme switching (System / Light / Dark) persisted via DataStore
- **Boosted Tokens** -- Horizontal carousel of top 10 DexScreener-boosted tokens with banner images
- **Avatars** -- Token images loaded from DexScreener API via Coil (fallback to symbol text)

## Documentation

| Document | Description |
|----------|-------------|
| [Architecture](ARCHITECTURE.md) | MVVM + Clean Architecture, layer design, data flow |
| [API](API.md) | DexScreener endpoints, request/response formats |
| [Features](FEATURES.md) | Detailed feature descriptions and UX patterns |
| [Testing](TESTING.md) | TDD approach, unit and instrumented tests, mock strategy |
| [Setup](SETUP.md) | Prerequisites, build instructions, running the app |
| [Dependencies](DEPENDENCIES.md) | Full dependency table with versions and purposes |

## Tech Stack

Kotlin, Jetpack Compose, Material 3, Hilt, Retrofit, Room, Coil, DataStore, Coroutines, Navigation Compose.

## Quick Start

```bash
./gradlew assembleDebug
```

See [Setup Guide](SETUP.md) for detailed instructions.

## Author

Created by **Nort**
