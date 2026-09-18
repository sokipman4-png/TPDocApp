# Blockage Log

Blocker eksternal yang ditemui selama pengembangan, dan cara mengatasinya (jika ada).

| # | Blocker | Dampak | Solusi | Status |
|---|---------|--------|--------|--------|
| - | Local build onmogelijk: Termux heeft geen Android SDK / emulator | Build & test-loop moet via GitHub Actions | Werd al gebruikt vanaf Tahap 1 (zie skill android-apk-build-ghactions) | Closed |
| 1 | CI run 35377072588 (commit 572aaa5) FAILED: ExportViewModel gebruikte niet-bestaande API (`androidx.unit.Unit`, `androidx.core.net.Uri`, `Application.startActivityForResult`, `Uri.getContentHub`) + JsonCodec nullable `.toLong()` + DashboardScreen missende @OptIn + Detail nullability | Compile-error hele app; APK niet gebouwd | Rewrite ExportViewModel met standaard API (Compose activity launchers + ContentResolver); fixes in JsonCodec/Dashboard/Detail; nieuwe tests | Closed — zie cleanup_report.md |
| 2 | Tahap 2.6 STEP 6: Robolectric/device UI-tests niet mogelijk op deze CI (Termux heeft geen Android SDK; GH Actions heeft geen Android-testframework voor Compose UI) | Geen UI-robot-test voor "tik + → simpen" etc. | De regressie-risico's zijn afgedekt door pure-logic tests (FormValidationTest, ShareIntentTest, OnboardingFlowTest e.d.) + bron-scan tests; handmatige UI-test op de telefoon blijft nodig | Open (mitigatie) |

Catatan: setiap blocker baru akan ditambahkan di sini lalu dikerjakan ulang setelahnya.