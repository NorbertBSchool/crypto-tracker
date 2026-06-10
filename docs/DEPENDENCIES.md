# Dependencies

## Core

| Library | Version | Purpose |
|---------|---------|---------|
| Kotlin | 2.3.21 | Programming language |
| Compose BOM | 2026.05.01 | UI framework (manages Compose versions) |
| Material 3 | (via BOM) | Design system |
| Core KTX | 1.15.0 | Kotlin extensions for Android |
| Activity Compose | 1.13.0 | Compose integration with Activity |

## Architecture

| Library | Version | Purpose |
|---------|---------|---------|
| Hilt | 2.58 | Dependency injection |
| Hilt Navigation Compose | 1.3.0 | ViewModel injection in Compose |
| Lifecycle ViewModel Compose | 2.10.0 | ViewModel integration with Compose |
| Lifecycle Runtime KTX | 2.10.0 | Lifecycle-aware coroutines |

## Networking

| Library | Version | Purpose |
|---------|---------|---------|
| Retrofit | 3.0.0 | HTTP client / REST API |
| Retrofit Gson Converter | 3.0.0 | JSON serialization/deserialization |
| OkHttp | 5.3.2 | HTTP engine |
| OkHttp Logging Interceptor | 5.3.2 | HTTP request/response logging |

## Local Storage

| Library | Version | Purpose |
|---------|---------|---------|
| Room Runtime | 2.7.0 | Local SQLite database |
| Room KTX | 2.7.0 | Coroutine support for Room |
| Room Compiler | 2.7.0 | Annotation processing (KSP) |
| DataStore Preferences | 1.1.7 | Key-value persistence (theme settings) |

## UI

| Library | Version | Purpose |
|---------|---------|---------|
| Navigation Compose | 2.7.7 | Screen navigation |
| Coil Compose | 2.7.0 | Image loading (token avatars) |
| WebKit | 1.12.1 | WebView (DexScreener chart embed) |
| Material Icons Core | (via BOM) | Icon set |
| Material Icons Extended | (via BOM) | Extended icon set |

## Coroutines

| Library | Version | Purpose |
|---------|---------|---------|
| Kotlinx Coroutines Android | 1.11.0 | Coroutine dispatchers for Android |
| Kotlinx Coroutines Test | 1.11.0 | Test utilities for coroutines |

## Testing

| Library | Version | Purpose |
|---------|---------|---------|
| JUnit 4 | 4.13.2 | Unit testing framework |
| Mockito Core | 5.11.0 | Mocking framework |
| Mockito Kotlin | 5.3.1 | Kotlin-friendly Mockito extensions |
| Turbine | 1.1.0 | Flow testing utility |
| Espresso Core | 3.7.0 | UI testing framework (declared, not actively used) |
| AndroidX JUnit | 1.3.0 | AndroidX test extensions |
| Compose UI Test JUnit4 | (via BOM) | Compose testing |
| Compose UI Test Manifest | (via BOM) | Compose test manifest |
| Hilt Android Testing | 2.58 | Hilt testing support |

## Build

| Plugin | Version | Purpose |
|--------|---------|---------|
| AGP | 8.13.2 | Android Gradle Plugin |
| Kotlin | 2.3.21 | Kotlin compiler |
| KSP | 2.3.9 | Kotlin Symbol Processing (Room, Hilt) |
| Compose Compiler | (via Kotlin) | Compose compiler plugin |

## Version Catalog

All versions are managed in `gradle/libs.versions.toml` for centralized dependency management.
