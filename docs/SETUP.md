# Setup

## Prerequisites

- **Android Studio** Hedgehog (2023.1.1) or newer
- **JDK 17** or newer
- **Android SDK** 35 (API 35)
- **Kotlin** 2.3.21 (configured in build.gradle.kts)

## Building

### Debug Build

```bash
./gradlew assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`

### Release Build

```bash
./gradlew assembleRelease
```

Note: Release builds require signing configuration. Add a `signing` block to `app/build.gradle.kts` or sign via Android Studio.

### Clean Build

```bash
./gradlew clean assembleDebug
```

## Running

### Android Studio

1. Open project in Android Studio
2. Select target device (emulator or physical device)
3. Click **Run** (green play button) or press `Shift+F10`

### Command Line

```bash
./gradlew installDebug
```

This installs the debug APK on a connected device/emulator.

## Device Requirements

- **Minimum SDK:** 26 (Android 8.0)
- **Target SDK:** 35 (Android 15)
- **Recommended:** API 30+ for full Material 3 support

## Permissions

The app requires **internet permission** to fetch data from DexScreener API. This is declared in `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## Data Storage

- **Room Database:** `cryptotracker_database` (auto-created, version 2)
  - Tables: `favorites`, `holdings`
  - Migration 1->2: adds `holdings` table
- **DataStore:** `settings.preferences_pb` (auto-created)
  - Stores theme preference (System/Light/Dark)

Both are created automatically on first launch -- no manual setup needed.

## Network

- Base URL: `https://api.dexscreener.com/`
- No API key required
- 30-second connect/read timeout (configured in `AppModule`)
- HTTP logging enabled in debug builds (via `HttpLoggingInterceptor`)

## Troubleshooting

### Build fails with "Could not resolve..." dependencies

```bash
./gradlew clean build --refresh-dependencies
```

### Hilt compilation errors

Ensure `ksp` plugin is applied and `hilt-compiler` is in `kapt`/`ksp` dependencies.

### Room migration errors

If you see `IllegalStateException: Room cannot verify the data integrity`, uninstall the app first:

```bash
adb uninstall com.example.cryptotracker
```

Then reinstall. This clears the old database schema.

## Continuous Integration

Two GitHub Actions workflows are configured in `.github/workflows/`:

### Unit Tests (`test.yml`)

- **Triggers:** Every push + every PR to `main`
- **Runs:** `./gradlew testDebugUnitTest` (35 JUnit tests, no emulator needed)
- **Environment:** Ubuntu, JDK 17
- **Artifacts:** Uploads test reports on failure

### Release APK (`release.yml`)

- **Triggers:** Push of a version tag (e.g. `v1.0.0`, `v2.1.3`)
- **Builds:** Debug APK (`assembleDebug`)
- **Publishes:** GitHub Release with APK attached + auto-generated release notes

### Creating a Release

```bash
git tag v1.0.0
git push origin v1.0.0
```

### Notes

- Release builds use the debug signing key since no `signingConfig` is configured for the `release` build type
- For properly signed release APKs, add a `signingConfig` block to `app/build.gradle.kts` and store the keystore as a GitHub Actions secret
