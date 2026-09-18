# Blockage Log

Blocker eksternal yang ditemui selama pengembangan, dan cara mengatasinya (jika ada).

| # | Blocker | Dampak | Solusi | Status |
|---|---------|--------|--------|--------|
| - | Local build onmogelijk: Termux heeft geen Android SDK / emulator | Build & test-loop moet via GitHub Actions | Werd al gebruikt vanaf Tahap 1 (zie skill android-apk-build-ghactions) | Closed |
| 1 | CI run 35377072588 (commit 572aaa5) FAILED: ExportViewModel gebruikte niet-bestaande API (`androidx.unit.Unit`, `androidx.core.net.Uri`, `Application.startActivityForResult`, `Uri.getContentHub`) + JsonCodec nullable `.toLong()` + DashboardScreen missende @OptIn + Detail nullability | Compile-error hele app; APK niet gebouwd | Rewrite ExportViewModel met standaard API (Compose activity launchers + ContentResolver); fixes in JsonCodec/Dashboard/Detail; nieuwe tests | Closed — zie cleanup_report.md |

Catatan: setiap blocker baru akan ditambahkan di sini lalu dikerjakan ulang setelahnya.