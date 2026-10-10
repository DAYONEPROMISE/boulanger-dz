# LIMITATIONS, COSTS, FIRST-10-CUSTOMERS

## Limitations (honest)
- NOT delivered: GitHub repo, CI run, APK. This runtime has no working shell
  (PowerShell bash tool times out on every command), no JVM/Android SDK,
  no `gh`, no GitHub MCP tools, no `openbot_browser` tool — only file+web tools.
  So: no `gh repo create`, no `git push`, no `./gradlew` run, no artifact.
- Code is complete on local disk and math hand-verified, but uncompiled;
  expect first CI run to surface minor issues (KSP/Room versions, icon lambda).
  NavigationBarItem `icon={}` compiles (empty lambda) but shows no icon — add
  Material icons before release.
- No instrumentation tests; Room DAOs untested (add robolectric or device tests).
- Arabic strings are minimal (3 keys); full translation + RTL screenshot review pending.
- No signing / Play release; debug APK only by design.

## Costs
- Dev: $0 (MIT libs, GitHub Actions free minutes for public repo).
- Runtime: $0 (100% offline, no backend).
- Optional: Play one-time $25 if store-listed later.

## First-10-customers plan (Algiers/Oran/Constantine bakeries)
1. Print FR/AR one-pager; visit 10 corner bakeries at 6h with demo phone.
2. Offer free install + preset their khobz/baguette formula (10 min).
3. WhatsApp support group; iterate hydration defaults from feedback.
4. Ask 3 for video testimonial → use for next 20. Price later: 2000 DZD/mo
   or free + paid flour-supplier leads. Never promise cloud sync (offline-only).
