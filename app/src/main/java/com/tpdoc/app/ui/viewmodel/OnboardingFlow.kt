package com.tpdoc.app.ui.viewmodel

/**
 * Flow onboardingsscherm — pure, JVM-testable (STEP 5.A).
 *
 * 4 glij-vensters:
 *  1. "Selamat Datang di TP Doc Manager" + illustratie + "Lewati"/"Lanjut"
 *  2. "Kelola Banyak Perusahaan" — multi-small cap + hiërarchie
 *  3. "Analisis TP Doc met of zonder AI" — dual mode
 *  4. "Siap Mulai" — "Mulai Sekarang" + optie "Muat Data Contoh"
 *
 * Na afronding wordt flag onboarding_done=true opgeslagen (AppMeta).
 */
data class OnboardingUiState(
    val page: Int = 0,
    val done: Boolean = false,
)

object OnboardingFlow {

    const val TOTAL_PAGES = 4

    const val SLIDE_1_TITLE = "Selamat Datang di TP Doc Manager"
    const val SLIDE_2_TITLE = "Kelola Banyak Perusahaan"
    const val SLIDE_3_TITLE = "Analisis TP Doc dengan atau tanpa AI"
    const val SLIDE_4_TITLE = "Siap Mulai"

    fun pageTitles(): List<String> = listOf(SLIDE_1_TITLE, SLIDE_2_TITLE, SLIDE_3_TITLE, SLIDE_4_TITLE)

    fun next(state: OnboardingUiState): OnboardingUiState =
        OnboardingUiState(page = minOf(state.page + 1, TOTAL_PAGES - 1))

    fun skip(state: OnboardingUiState): OnboardingUiState =
        OnboardingUiState(page = TOTAL_PAGES - 1)

    fun finish(state: OnboardingUiState): OnboardingUiState =
        OnboardingUiState(page = state.page, done = true)

    fun isLast(state: OnboardingUiState): Boolean = state.page >= TOTAL_PAGES - 1

    fun isValidPage(page: Int): Boolean = page >= 0 && page < TOTAL_PAGES
}