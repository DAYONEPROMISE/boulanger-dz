# OpenPencil pass — boulanger-dz

## Design (Shared/OpenPencil/boulanger-dz/)
- `design.html` (recipe scaler + result, bakery-amber tokens) → `design.fig` via `openpencil import`
- `lint --preset accessibility`: 0 errors, 0 warnings

## Code review (no rewrite needed)
`MainActivity.kt` + `Theme.kt` already meet the 2026 standard: bakery-amber light/dark schemes
with dynamic color, `enableEdgeToEdge`, adaptive rail (≥600dp) / bottom bar, icon
`contentDescription`s, heading semantics. No code change — forcing edits would only risk a green build.

## APKs
`android-build.yml` now also runs `:app:assembleRelease` (unsigned) and uploads
`boulangerdz-release-apk` alongside `boulangerdz-debug-apk`. Both per workflow run in Artifacts.
