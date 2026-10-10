# TEST_TABLE.md

| # | Test | Input | Expected | Type |
|---|------|-------|----------|------|
| 1 | flour=1000 at 65-2-1 | fromFlour(1000,65,2,1) | water 650, salt 20, yeast 10, total 1680 | unit |
| 2 | target round-trip | fromTargetDough(1680,65,2,1) | flour ≈1000, total ≈1680 | unit |
| 3 | 100×250g khobz | fromUnits(100,250,60,2,1.2) | total 25000, flour ≈15319 | unit |
| 4 | zero target rejected | fromTargetDough(0,…) | IllegalArgumentException | unit |
| 5 | hydration rejected | fromTargetDough(1000,150,…) | IllegalArgumentException | unit |
| 6 | baguette preset | fromFlour(1000,68,2,0.7) | water 680, yeast 7 | unit |

Execution: `./gradlew :app:testDebugUnitTest` in CI. Status: NOT EXECUTED here
(no JVM/Android SDK in this runtime; PowerShell `bash` tool hangs — see Limitations).
Manual verification of math: T1: 1000×0.65=650 ✓; divisor 1.68 → 1680/1.68=1000 ✓;
T3: divisor 1+0.6+0.02+0.012=1.632 → 25000/1.632=15318.6 ✓.
