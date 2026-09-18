package com.tpdoc.app

import com.tpdoc.app.ui.viewmodel.OnboardingFlow
import com.tpdoc.app.ui.viewmodel.OnboardingUiState
import org.junit.Assert.*
import org.junit.Test

/**
 * Test onboarding flow (STEP 5.A Tahap 2.6):
 * 4 glijden verschijnen bij eerste open; volgende/vorige navigatie werkt;
 * afronden zet done flag.
 */
class OnboardingFlowTest {

    @Test
    fun `onboarding heeft 4 pagina's`() {
        assertEquals(4, OnboardingFlow.TOTAL_PAGES)
        assertEquals(4, OnboardingFlow.pageTitles().size)
        assertEquals("Selamat Datang di TP Doc Manager", OnboardingFlow.pageTitles().first())
    }

    @Test
    fun `eerste open toont pagina 1`() {
        val start = OnboardingUiState()
        assertEquals(0, start.page)
        assertFalse(start.done)
        assertFalse(OnboardingFlow.isLast(start))
        assertTrue(OnboardingFlow.isValidPage(start.page))
    }

    @Test
    fun `lanjut gaat naar volgende pagina`() {
        val s1 = OnboardingFlow.next(OnboardingUiState())
        assertEquals(1, s1.page)
        val s2 = OnboardingFlow.next(s1)
        assertEquals(2, s2.page)
        val s3 = OnboardingFlow.next(s2)
        assertEquals(3, s3.page)
        assertTrue(OnboardingFlow.isLast(s3))
    }

    @Test
    fun `lanjut op laatste pagina blijft staan (geen overflow)`() {
        val last = OnboardingUiState(page = 3)
        val next = OnboardingFlow.next(last)
        assertEquals(3, next.page)
        assertTrue(OnboardingFlow.isLast(next))
    }

    @Test
    fun `lewati springt naar laatste pagina zonder markering`() {
        val skipped = OnboardingFlow.skip(OnboardingUiState())
        assertEquals(OnboardingFlow.TOTAL_PAGES - 1, skipped.page)
        assertFalse(skipped.done)
    }

    @Test
    fun `afronden zet done flag`() {
        val done = OnboardingFlow.finish(OnboardingUiState(page = 3))
        assertTrue(done.done)
        assertEquals(3, done.page)
    }

    @Test
    fun `pagina validatie`() {
        assertFalse(OnboardingFlow.isValidPage(-1))
        assertFalse(OnboardingFlow.isValidPage(4))
        assertTrue(OnboardingFlow.isValidPage(0))
        assertTrue(OnboardingFlow.isValidPage(3))
    }
}