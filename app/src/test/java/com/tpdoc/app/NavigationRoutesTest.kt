package com.tpdoc.app

import com.tpdoc.app.navigation.Routes
import org.junit.Assert.*
import org.junit.Test

class NavigationRoutesTest {

    @Test
    fun `semua tab routes didaflar di statics`() {
        Routes.TABS.forEach { route ->
            assertTrue("Tab $route wajib didaflar", route in Routes.STATICS)
        }
    }

    @Test
    fun `semua tab routes ada di all patterns`() {
        Routes.TABS.forEach { route ->
            assertTrue("Tab $route wajib di ALL_PATTERNS", route in Routes.ALL_PATTERNS)
        }
    }

    @Test
    fun `rute patterns unik - tidak ada duplikat`() {
        val patterns = Routes.ALL_PATTERNS.toList()
        assertEquals("Pattern wajib unik", patterns.size, patterns.distinct().size)
    }

    @Test
    fun `start destination adalah daftar perusahaan`() {
        // Pastikan start route didaflar di NavHost (DaftarPerusahaan adalah tab)
        assertTrue("Start route wajib tab", Routes.DAFTAR_PERUSAHAAN in Routes.TABS)
    }

    @Test
    fun `builder rute detail perusahaan matches pattern`() {
        val route = Routes.detailPerusahaan(42)
        assertEquals("detail_perusahaan/42", route)
        assertTrue(isPatternMatch(Routes.DETAIL_PERUSAHAAN, route))
    }

    @Test
    fun `builder rute form perusahaan matches pattern`() {
        val route = Routes.formPerusahaan(7)
        assertTrue(isPatternMatch(Routes.FORM_PERUSAHAAN, route))
        // Form kosong = create
        assertTrue(isPatternMatch(Routes.FORM_PERUSAHAAN, Routes.formPerusahaan(null)))
    }

    @Test
    fun `builder rute analisis matches pattern`() {
        val route = Routes.analisis(3)
        assertEquals("analisis/3", route)
        assertTrue(isPatternMatch(Routes.ANALISIS, route))
    }

    @Test
    fun `isTab berfungsi untuk tab en dah untuk detail`() {
        assertTrue(Routes.isTab(Routes.DAFTAR_PERUSAHAAN))
        assertTrue(Routes.isTab(Routes.HOME))
        assertFalse(Routes.isTab(Routes.DASHBOARD))
        assertFalse(Routes.isTab("detail_perusahaan/1"))
    }

    private fun isPatternMatch(pattern: String, route: String): Boolean {
        val patternParts = pattern.split("/")
        val routeParts = route.split("/")
        if (patternParts.size != routeParts.size) return false
        for (i in 0..patternParts.size - 1) {
            if (patternParts[i].startsWith("{") && patternParts[i].endsWith("}")) continue
            if (patternParts[i] != routeParts[i]) return false
        }
        return true
    }
}