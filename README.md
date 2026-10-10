# BoulangerDZ — offline recipe scaler + production planner (MIT, clean-room)

Offline-first Android app (Kotlin, Compose Material 3, Room). No account, no network.
FR/AR with RTL (`supportsRtl=true`, start/end layouts, `values-fr`, `values-ar`).

## Features
- Recipe scaler: baker's % (flour=100%), presets Khobz 60-65% / Baguette 65-70%.
- Daily planner: units × weight → flour/water/salt/yeast, stored in Room.
- Stock notes: ingredient + qty, offline.

## Stack (pinned, verified Oct 2026)
AGP 8.5.2, Kotlin 1.9.24, KSP 1.9.24-1.0.20, compile/target 34, min 24,
Compose BOM 2024.06.00, material3 1.2.1, Room 2.6.1, JUnit 4.13.2.

## Build
```
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```
APK: `app/build/outputs/apk/debug/app-debug.apk`

## Tests
6 unit tests in `RecipeScalerTest` (see TEST_TABLE.md). JVM-only, no device needed.

## Research
See RESEARCH.md. Sources: baker's-% explainer, baguette formulas (~65-75% hydro,
~2% salt, 0.4-1% yeast), khobz/mat loua variants, Android RTL + localization docs.
