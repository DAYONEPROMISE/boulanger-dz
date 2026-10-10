# RESEARCH.md — BoulangerDZ (verified 2026-10-10)

## 1. Baker's math
- Flour always 100%; water/salt/yeast as % of flour. Total % ≠ 100.
  Verified: https://bakecalc.club/articles/bakers-percentage-explained
  Baseline remembered: 100% flour / ~65% water / 2% salt / 1% yeast → 168% total.
- Calculator pattern (balls × weight → grams): https://dough-calc.com/bagels/sd-bagel
- Generic calculator: https://www.gigacalculator.com/calculators/bread-calculator.php

## 2. Algerian products
- Khobz / matloua (kesra/agroum): flour + semolina, ~52-56% hydration in home
  recipes, 0.5 tsp yeast per ~240g flour, oil, sugar; commercial khobz runs
  wetter (~60-65%). Sources:
  https://falktime.com/Khobz/ and https://www.youtube.com/watch?v=0rOVAiPcdUs
- Baguette: 65-75% hydration standard, salt ~2-2.1%, dry yeast ~0.4-1%.
  Sources: https://www.weekendbakery.com/posts/recipe-for-80-hydration-baguette,
  https://food52.com/recipes/36381-homemade-baguettes,
  https://dawghousebakery.com/2021/02/27/jeffrey-hamelmans-poolish-baguette-adapted
- Decision: presets Khobz (62%/2%/1.2%) and Baguette (68%/2%/0.7%), user-editable.
  Home-recipe hydrations are understated (hand-knead bias); bakery dough is wetter.

## 3. Android stack
- Compose-first is official: https://m3.material.io/develop/android/jetpack-compose
- Compose Dec'25 stable = core 1.10 / M3 1.4, BOM 2025.12.00:
  https://developer.android.com/blog/posts/whats-new-in-the-jetpack-compose-december-release
- M3 versions list (1.4.0 stable Sep 2025; 1.5 alphas current):
  https://mvnrepository.com/artifact/androidx.compose.material3/material3/versions
- Decision: pin conservative BOM 2024.06.00 + M3 1.2.1 + AGP 8.5.2 + Kotlin 1.9.24
  for max CI cache-hit reliability on ubuntu-latest; avoids alpha APIs.

## 4. Offline + RTL/FR-AR
- Localize with values-fr / values-ar + fallback:
  https://developer.android.com/guide/topics/resources/localization.html
- RTL: supportsRtl=true, use start/end not left/right, ldrtl qualifiers,
  Compose mirrors automatically:
  https://developer.android.com/studio/write/translations-editor
  https://developer.android.com/training/basics/supporting-devices/languages.html
- Pinned Room 2.6.1 (stable, KSP). No Firebase (offline requirement).
